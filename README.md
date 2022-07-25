# SMS Forward

SMS Forward is a simple app that allows you to redirect SMS you receive on your phone to another device.

This application have been developed to bypass regional limitations.
Example: a service in country A requires a phone number from country A, but your phone number is from country B. You ask a friend from country A to install this app and you will be able to receive SMS from the service in country B. However, since this is a pretty simple application, it can also serve other purposes.

Features:
- Can handle multiple redirections
- Global activation toggle

SMS Forward is free, open source and without ads.

It runs on Android 5.0 "Lollipop" (API 21) and later.

This application is built using MVVM architecture.

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/com.pierreduchemin.smsforward/)

## Build instructions

Build with:
```bash
./gradlew assembleRelease
```

## Testing

Given the nature of this project, testing can be challenging.
A good option is to use services like [TextNow](https://www.textnow.com) to get a phone number and start testing sms redirections.

## Contributing

Any contributions welcome. Please fork this repository and create a pull request notifying your changes and why.

## Author

**Pierre Duchemin**

## License

SMS Forward's [LICENSE](LICENSE) is GNU GPL v3+.
