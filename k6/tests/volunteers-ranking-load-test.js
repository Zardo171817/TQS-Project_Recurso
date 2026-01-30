import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';
import { BASE_URL, OPTIONS_LOAD } from '../config.js';
import {
    randomEmail,
    randomString,
    randomInt,
    registerUser,
    loginUser,
    createVolunteerProfile
} from '../utils/helpers.js';

// Custom metrics
const profileListErrors = new Rate('profile_list_errors');
const rankingErrors = new Rate('ranking_errors');

export const options = OPTIONS_LOAD;

export function setup() {
    console.log(`Starting volunteers and ranking load test against ${BASE_URL}`);

    // Create multiple test volunteers for ranking
    const volunteers = [];

    for (let i = 0; i < 5; i++) {
        const email = `volunteer_${randomString(10)}@test.com`;
        const userData = {
            email: email,
            password: 'Test@1234',
            name: `Test Volunteer ${i + 1}`,
            userType: 'VOLUNTEER'
        };

        registerUser(BASE_URL, userData);
        const volunteer = loginUser(BASE_URL, email, 'Test@1234');

        if (volunteer) {
            // Create profile for each volunteer
            createVolunteerProfile(BASE_URL, volunteer.id, {
                skills: ['Communication', 'Leadership', 'Teamwork'],
                interests: ['Education', 'Environment'],
                availability: ['WEEKDAYS', 'WEEKENDS', 'EVENINGS'][i % 3]
            });

            volunteers.push(volunteer);
        }
    }

    console.log(`Setup complete. Created ${volunteers.length} test volunteers`);

    return {
        baseUrl: BASE_URL,
        volunteerIds: volunteers.map(v => v.id)
    };
}

export default function (data) {
    if (!data || !data.volunteerIds || data.volunteerIds.length === 0) {
        console.error('Setup failed or no volunteers available, skipping test iteration');
        return;
    }

    const baseUrl = data.baseUrl;

    // Test 1: List all volunteer profiles
    const listResponse = http.get(`${baseUrl}/api/volunteers/profiles`);
    const listSuccess = check(listResponse, {
        'list profiles successful': (r) => r.status === 200,
        'profiles array returned': (r) => Array.isArray(r.json()),
    });
    profileListErrors.add(!listSuccess);

    sleep(1);

    // Test 2: Get volunteer ranking (computationally intensive)
    const rankingResponse = http.get(`${baseUrl}/api/volunteers/ranking`);
    const rankingSuccess = check(rankingResponse, {
        'get ranking successful': (r) => r.status === 200,
        'ranking array returned': (r) => Array.isArray(r.json()),
    });
    rankingErrors.add(!rankingSuccess);

    sleep(1);

    // Test 3: Get top N volunteers
    const topN = randomInt(3, 10);
    const topResponse = http.get(`${baseUrl}/api/volunteers/top/${topN}`);
    check(topResponse, {
        'get top volunteers successful': (r) => r.status === 200,
        'top volunteers returned': (r) => Array.isArray(r.json()),
        'correct number returned': (r) => r.json().length <= topN,
    });

    sleep(1);

    // Test 4: Get specific volunteer details
    const randomVolunteerId = data.volunteerIds[Math.floor(Math.random() * data.volunteerIds.length)];

    const volunteerResponse = http.get(`${baseUrl}/api/volunteers/${randomVolunteerId}`);
    check(volunteerResponse, {
        'get volunteer successful': (r) => r.status === 200,
        'volunteer has id': (r) => r.json('id') !== undefined,
    });

    sleep(1);

    // Test 5: Get volunteer points
    const pointsResponse = http.get(`${baseUrl}/api/volunteers/${randomVolunteerId}/points`);
    check(pointsResponse, {
        'get points successful': (r) => r.status === 200,
        'points is a number': (r) => typeof r.json() === 'number' || r.json('points') !== undefined,
    });

    sleep(1);

    // Test 6: Get volunteer confirmed participations
    const participationsResponse = http.get(`${baseUrl}/api/volunteers/${randomVolunteerId}/confirmed-participations`);
    check(participationsResponse, {
        'get participations successful': (r) => r.status === 200,
        'participations array returned': (r) => Array.isArray(r.json()),
    });

    sleep(1);

    // Test 7: Get volunteer points history
    const historyResponse = http.get(`${baseUrl}/api/volunteers/${randomVolunteerId}/points-history`);
    check(historyResponse, {
        'get points history successful': (r) => r.status === 200,
        'history array returned': (r) => Array.isArray(r.json()),
    });

    sleep(1);

    // Test 8: Search profiles by skills
    const skills = ['Communication', 'Leadership', 'Teamwork', 'Problem Solving'];
    const randomSkill = skills[Math.floor(Math.random() * skills.length)];

    const skillsResponse = http.get(`${baseUrl}/api/volunteers/profiles/skills/${randomSkill}`);
    check(skillsResponse, {
        'search by skills successful': (r) => r.status === 200,
        'profiles with skill returned': (r) => Array.isArray(r.json()),
    });

    sleep(1);

    // Test 9: Search profiles by interests
    const interests = ['Education', 'Environment', 'Health', 'Social'];
    const randomInterest = interests[Math.floor(Math.random() * interests.length)];

    const interestsResponse = http.get(`${baseUrl}/api/volunteers/profiles/interests/${randomInterest}`);
    check(interestsResponse, {
        'search by interests successful': (r) => r.status === 200,
        'profiles with interest returned': (r) => Array.isArray(r.json()),
    });

    sleep(1);

    // Test 10: Search profiles by availability
    const availabilities = ['WEEKDAYS', 'WEEKENDS', 'EVENINGS'];
    const randomAvailability = availabilities[Math.floor(Math.random() * availabilities.length)];

    const availabilityResponse = http.get(`${baseUrl}/api/volunteers/profiles/availability/${randomAvailability}`);
    check(availabilityResponse, {
        'search by availability successful': (r) => r.status === 200,
        'profiles with availability returned': (r) => Array.isArray(r.json()),
    });

    sleep(2);
}

export function teardown(data) {
    console.log('Volunteers and ranking load test completed');
}
