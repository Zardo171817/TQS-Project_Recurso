// K6 Load Test Configuration

export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Test users data
export const TEST_USERS = {
    volunteer: {
        email: 'volunteer.test@example.com',
        password: 'Test@1234',
        name: 'Test Volunteer',
        userType: 'VOLUNTEER'
    },
    promoter: {
        email: 'promoter.test@example.com',
        password: 'Test@1234',
        name: 'Test Promoter',
        userType: 'PROMOTER'
    },
    partner: {
        email: 'partner.test@example.com',
        password: 'Test@1234',
        name: 'Test Partner',
        userType: 'PARTNER'
    }
};

// Load test scenarios
export const SCENARIOS = {
    smoke: {
        stages: [
            { duration: '1m', target: 1 },
        ],
    },
    load: {
        stages: [
            { duration: '2m', target: 10 },
            { duration: '5m', target: 10 },
            { duration: '2m', target: 0 },
        ],
    },
    stress: {
        stages: [
            { duration: '2m', target: 10 },
            { duration: '5m', target: 20 },
            { duration: '2m', target: 30 },
            { duration: '5m', target: 30 },
            { duration: '2m', target: 0 },
        ],
    },
    spike: {
        stages: [
            { duration: '10s', target: 5 },
            { duration: '1m', target: 50 },
            { duration: '10s', target: 5 },
            { duration: '3m', target: 5 },
            { duration: '10s', target: 0 },
        ],
    },
    soak: {
        stages: [
            { duration: '2m', target: 10 },
            { duration: '3h', target: 10 },
            { duration: '2m', target: 0 },
        ],
    }
};

// Thresholds for performance metrics
export const THRESHOLDS = {
    http_req_failed: ['rate<0.01'], // Less than 1% of requests should fail
    http_req_duration: ['p(95)<500', 'p(99)<1000'], // 95% of requests should be below 500ms, 99% below 1s
    http_reqs: ['rate>10'], // Minimum of 10 requests per second
};

export const OPTIONS_SMOKE = {
    stages: SCENARIOS.smoke.stages,
    thresholds: THRESHOLDS,
};

export const OPTIONS_LOAD = {
    stages: SCENARIOS.load.stages,
    thresholds: THRESHOLDS,
};

export const OPTIONS_STRESS = {
    stages: SCENARIOS.stress.stages,
    thresholds: {
        http_req_failed: ['rate<0.05'], // Allow up to 5% failure in stress test
        http_req_duration: ['p(95)<1000', 'p(99)<2000'],
    },
};

export const OPTIONS_SPIKE = {
    stages: SCENARIOS.spike.stages,
    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<1000'],
    },
};

export const OPTIONS_SOAK = {
    stages: SCENARIOS.soak.stages,
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<500', 'p(99)<1000'],
    },
};
