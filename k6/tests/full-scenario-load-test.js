import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Counter, Rate } from 'k6/metrics';
import { BASE_URL, OPTIONS_LOAD } from '../config.js';
import {
    randomEmail,
    randomString,
    randomInt,
    randomChoice,
    registerUser,
    loginUser,
    createOpportunity,
    createApplication,
    createVolunteerProfile,
    createBenefit
} from '../utils/helpers.js';

// Custom metrics
const scenarioCompletions = new Counter('scenario_completions');
const scenarioErrors = new Rate('scenario_errors');

export const options = OPTIONS_LOAD;

export function setup() {
    console.log(`Starting full scenario load test against ${BASE_URL}`);
    return { baseUrl: BASE_URL };
}

// Simulates a complete user journey through the platform
export default function (data) {
    const baseUrl = data.baseUrl;
    let scenarioFailed = false;

    // Scenario 1: Volunteer Journey (60% of users)
    if (Math.random() < 0.6) {
        group('Volunteer User Journey', () => {
            // Step 1: Register as volunteer
            const volunteerEmail = randomEmail();
            const volunteerData = {
                email: volunteerEmail,
                password: 'Test@1234',
                name: `Volunteer ${randomString(6)}`,
                userType: 'VOLUNTEER'
            };

            const registerResponse = registerUser(baseUrl, volunteerData);
            if (registerResponse.status !== 201 && registerResponse.status !== 200) {
                scenarioFailed = true;
                return;
            }

            sleep(1);

            // Step 2: Login
            const volunteer = loginUser(baseUrl, volunteerEmail, 'Test@1234');
            if (!volunteer) {
                scenarioFailed = true;
                return;
            }

            sleep(1);

            // Step 3: Create profile
            createVolunteerProfile(baseUrl, volunteer.id, {
                skills: randomChoice([
                    ['Communication', 'Leadership'],
                    ['Teamwork', 'Problem Solving'],
                    ['Technical Skills', 'Creativity']
                ]),
                interests: randomChoice([
                    ['Education', 'Environment'],
                    ['Health', 'Social'],
                    ['Culture', 'Technology']
                ]),
                availability: randomChoice(['WEEKDAYS', 'WEEKENDS', 'EVENINGS'])
            });

            sleep(1);

            // Step 4: Browse opportunities
            const opportunitiesResponse = http.get(`${baseUrl}/api/opportunities`);
            if (opportunitiesResponse.status !== 200) {
                scenarioFailed = true;
                return;
            }

            const opportunities = opportunitiesResponse.json();

            sleep(2);

            // Step 5: Filter opportunities by category
            const category = randomChoice(['EDUCATION', 'HEALTH', 'ENVIRONMENT', 'SOCIAL', 'CULTURE']);
            http.get(`${baseUrl}/api/opportunities/filter?category=${category}`);

            sleep(1);

            // Step 6: Apply to an opportunity (if any exist)
            if (opportunities && opportunities.length > 0) {
                const randomOpp = opportunities[Math.floor(Math.random() * opportunities.length)];
                createApplication(baseUrl, volunteer.id, randomOpp.id);

                sleep(1);

                // Step 7: Check my applications
                http.get(`${baseUrl}/api/applications/volunteer/${volunteer.id}`);
            }

            sleep(1);

            // Step 8: Browse available benefits
            http.get(`${baseUrl}/api/benefits`);

            sleep(1);

            // Step 9: Check my points
            http.get(`${baseUrl}/api/volunteers/${volunteer.id}/points`);

            sleep(1);

            // Step 10: Check ranking
            http.get(`${baseUrl}/api/volunteers/ranking`);
        });
    }
    // Scenario 2: Promoter Journey (30% of users)
    else if (Math.random() < 0.75) { // 30% of total (0.4 * 0.75)
        group('Promoter User Journey', () => {
            // Step 1: Register as promoter
            const promoterEmail = randomEmail();
            const promoterData = {
                email: promoterEmail,
                password: 'Test@1234',
                name: `Promoter ${randomString(6)}`,
                userType: 'PROMOTER'
            };

            const registerResponse = registerUser(baseUrl, promoterData);
            if (registerResponse.status !== 201 && registerResponse.status !== 200) {
                scenarioFailed = true;
                return;
            }

            sleep(1);

            // Step 2: Login
            const promoter = loginUser(baseUrl, promoterEmail, 'Test@1234');
            if (!promoter) {
                scenarioFailed = true;
                return;
            }

            sleep(1);

            // Step 3: Create an opportunity
            const oppResponse = createOpportunity(baseUrl, promoter.id, {
                category: randomChoice(['EDUCATION', 'HEALTH', 'ENVIRONMENT', 'SOCIAL', 'CULTURE']),
                numberOfVacancies: randomInt(5, 20),
                pointsAwarded: randomInt(50, 200)
            });

            let opportunityId = null;
            if (oppResponse.status === 201 || oppResponse.status === 200) {
                opportunityId = oppResponse.json().id;
            }

            sleep(2);

            // Step 4: View my opportunities
            http.get(`${baseUrl}/api/opportunities/promoter/${promoter.id}`);

            sleep(1);

            // Step 5: Check applications for my opportunities
            http.get(`${baseUrl}/api/applications/promoter/${promoter.id}`);

            sleep(1);

            // Step 6: View applications for specific opportunity (if created)
            if (opportunityId) {
                const appsResponse = http.get(`${baseUrl}/api/applications/opportunity/${opportunityId}`);

                sleep(1);

                // Step 7: Accept an application (if any exist)
                if (appsResponse.status === 200) {
                    const applications = appsResponse.json();
                    if (applications && applications.length > 0) {
                        const randomApp = applications[0];

                        const updatePayload = JSON.stringify({ status: 'ACCEPTED' });
                        const params = {
                            headers: { 'Content-Type': 'application/json' },
                        };

                        http.patch(`${baseUrl}/api/applications/${randomApp.id}/status`, updatePayload, params);
                    }
                }
            }

            sleep(1);

            // Step 8: Check concluded opportunities count
            http.get(`${baseUrl}/api/opportunities/promoter/${promoter.id}/concluded-count`);
        });
    }
    // Scenario 3: Partner Journey (10% of users)
    else {
        group('Partner User Journey', () => {
            // Step 1: Register as partner
            const partnerEmail = randomEmail();
            const partnerData = {
                email: partnerEmail,
                password: 'Test@1234',
                name: `Partner ${randomString(6)}`,
                userType: 'PARTNER'
            };

            const registerResponse = registerUser(baseUrl, partnerData);
            if (registerResponse.status !== 201 && registerResponse.status !== 200) {
                scenarioFailed = true;
                return;
            }

            sleep(1);

            // Step 2: Login
            const partner = loginUser(baseUrl, partnerEmail, 'Test@1234');
            if (!partner) {
                scenarioFailed = true;
                return;
            }

            sleep(1);

            // Step 3: Create a benefit
            createBenefit(baseUrl, partner.id, {
                category: randomChoice(['DISCOUNT', 'VOUCHER', 'EXPERIENCE']),
                pointsCost: randomInt(50, 500)
            });

            sleep(2);

            // Step 4: View my benefits
            http.get(`${baseUrl}/api/benefits/partner`);

            sleep(1);

            // Step 5: Check redemptions for my benefits
            http.get(`${baseUrl}/api/redemptions/partner/Test Provider`);

            sleep(1);

            // Step 6: View redemption statistics
            http.get(`${baseUrl}/api/redemptions/partner/Test Provider/stats`);
        });
    }

    if (!scenarioFailed) {
        scenarioCompletions.add(1);
    } else {
        scenarioErrors.add(1);
    }

    sleep(2);
}

export function teardown(data) {
    console.log('Full scenario load test completed');
}
