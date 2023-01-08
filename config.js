//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
module.exports = {
  endpoint: 'https://gitlab.com/api/v4/',
  token: process.env.RENOVATE_GITLAB_TOKEN,
  platform: 'gitlab',
  onboardingConfig: {
    extends: ['config:base'],
  },
  repositories: ['pierreduchemin/smsforward'],
};

