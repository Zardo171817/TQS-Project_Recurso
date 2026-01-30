import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Counter } from 'k6/metrics';
import { BASE_URL, OPTIONS_LOAD } from '../config.js';
import {
    randomEmail,
    randomString,
    randomInt,
    registerUser,
    loginUser,
    createBenefit
} from '../utils/helpers.js';

// Custom metrics
const benefitListErrors = new Rate('benefit_list_errors');
const redemptionErrors = new Rate('redemption_errors');
const redemptionsCreated = new Counter('redemptions_created');

export const options = OPTIONS_LOAD;

export function setup() {
    console.log(`Starting benefits and redemptions load test against ${BASE_URL}`);

    // Create a test partner
    const partnerEmail = `partner_${randomString(10)}@test.com`;
    const partnerData = {
        email: partnerEmail,
        password: 'Test@1234',
        name: 'Test Partner',
        userType: 'PARTNER'
    };

    registerUser(BASE_URL, partnerData);
    const partner = loginUser(BASE_URL, partnerEmail, 'Test@1234');

    if (!partner) {
        console.error('Failed to create test partner');
        return null;
    }

    // Create test benefits
    const benefits = [];
    const categories = ['DISCOUNT', 'VOUCHER', 'EXPERIENCE'];

    for (let i = 0; i < 10; i++) {
        const response = createBenefit(BASE_URL, partner.id, {
            category: categories[i % categories.length],
            pointsCost: randomInt(50, 500)
        });

        if (response.status === 201 || response.status === 200) {
            benefits.push(response.json());
        }
    }

    // Create a volunteer with points for redemptions
    const volunteerEmail = `volunteer_${randomString(10)}@test.com`;
    const volunteerData = {
        email: volunteerEmail,
        password: 'Test@1234',
        name: 'Test Volunteer',
        userType: 'VOLUNTEER'
    };

    registerUser(BASE_URL, volunteerData);
    const volunteer = loginUser(BASE_URL, volunteerEmail, 'Test@1234');

    console.log(`Setup complete. Created ${benefits.length} test benefits`);

    return {
        baseUrl: BASE_URL,
        partnerId: partner.id,
        benefits: benefits,
        volunteerId: volunteer ? volunteer.id : null
    };
}

export default function (data) {
    if (!data || !data.benefits || data.benefits.length === 0) {
        console.error('Setup failed or no benefits available, skipping test iteration');
        return;
    }

    const baseUrl = data.baseUrl;

    // Test 1: List all benefits
    const listResponse = http.get(`${baseUrl}/api/benefits`);
    const listSuccess = check(listResponse, {
        'list benefits successful': (r) => r.status === 200,
        'benefits array returned': (r) => Array.isArray(r.json()),
    });
    benefitListErrors.add(!listSuccess);

    sleep(1);

    // Test 2: Filter benefits by category
    const categories = ['DISCOUNT', 'VOUCHER', 'EXPERIENCE'];
    const randomCategory = categories[Math.floor(Math.random() * categories.length)];

    const categoryResponse = http.get(`${baseUrl}/api/benefits/category/${randomCategory}`);
    check(categoryResponse, {
        'filter by category successful': (r) => r.status === 200,
        'category benefits returned': (r) => Array.isArray(r.json()),
    });

    sleep(1);

    // Test 3: Get benefits sorted by points
    const sortedAscResponse = http.get(`${baseUrl}/api/benefits/sorted/points-asc`);
    check(sortedAscResponse, {
        'sorted asc successful': (r) => r.status === 200,
        'sorted benefits returned': (r) => Array.isArray(r.json()),
    });

    sleep(1);

    const sortedDescResponse = http.get(`${baseUrl}/api/benefits/sorted/points-desc`);
    check(sortedDescResponse, {
        'sorted desc successful': (r) => r.status === 200,
    });

    sleep(1);

    // Test 4: Get specific benefit details
    const randomBenefit = data.benefits[Math.floor(Math.random() * data.benefits.length)];
    const detailsResponse = http.get(`${baseUrl}/api/benefits/${randomBenefit.id}`);
    check(detailsResponse, {
        'get benefit details successful': (r) => r.status === 200,
        'benefit has required fields': (r) => {
            const benefit = r.json();
            return benefit.title && benefit.pointsCost !== undefined;
        },
    });

    sleep(1);

    // Test 5: Get benefits by provider
    const providerResponse = http.get(`${baseUrl}/api/benefits/provider/Test Provider`);
    check(providerResponse, {
        'get by provider successful': (r) => r.status === 200,
    });

    sleep(1);

    // Test 6: Get affordable benefits for volunteer (if volunteer exists)
    if (data.volunteerId) {
        const affordableResponse = http.get(`${baseUrl}/api/benefits/volunteer/${data.volunteerId}/affordable`);
        check(affordableResponse, {
            'get affordable benefits successful': (r) => r.status === 200,
        });

        sleep(1);

        // Test 7: Attempt to redeem a benefit (will likely fail due to insufficient points, but tests the endpoint)
        const redemptionPayload = JSON.stringify({
            volunteerId: data.volunteerId,
            benefitId: randomBenefit.id
        });

        const params = {
            headers: {
                'Content-Type': 'application/json',
            },
        };

        const redemptionResponse = http.post(`${baseUrl}/api/redemptions`, redemptionPayload, params);

        // Success or expected failure (insufficient points)
        const redemptionSuccess = redemptionResponse.status === 201 ||
                                  redemptionResponse.status === 200 ||
                                  redemptionResponse.status === 400;

        redemptionErrors.add(!redemptionSuccess);

        if (redemptionResponse.status === 201 || redemptionResponse.status === 200) {
            redemptionsCreated.add(1);
        }

        sleep(1);

        // Test 8: Get volunteer redemption history
        const historyResponse = http.get(`${baseUrl}/api/redemptions/volunteer/${data.volunteerId}`);
        check(historyResponse, {
            'get redemption history successful': (r) => r.status === 200,
            'redemptions array returned': (r) => Array.isArray(r.json()),
        });
    }

    sleep(2);
}

export function teardown(data) {
    console.log('Benefits and redemptions load test completed');
}
