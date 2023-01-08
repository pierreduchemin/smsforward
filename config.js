module.exports = {
  endpoint: 'https://gitlab.com/api/v4/',
  token: process.env.RENOVATE_GITLAB_TOKEN,
  platform: 'gitlab',
  onboardingConfig: {
    extends: ['config:base'],
  },
  repositories: ['pierreduchemin/smsforward'],
};

