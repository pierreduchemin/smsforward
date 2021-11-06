FROM thyrlian/android-sdk:8.0

RUN apt-get update
RUN DEBIAN_FRONTEND="noninteractive" apt-get install -y --no-install-recommends \
    build-essential \
    ruby3.0-dev \
	git \
	locales \
	unzip \
	wget \
	tzdata \
	&& locale-gen $LANG $LC_ALL && update-locale $LANG $LC_ALL

RUN gem install fastlane bundler

VOLUME /home/builder/src
