FROM ubuntu:22.10

ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8
ENV TZ=Etc/UTC

RUN apt-get update
RUN DEBIAN_FRONTEND="noninteractive" apt-get install -y --no-install-recommends \
    build-essential \
    ruby3.0-dev \
	git \
	locales \
	openjdk-11-jdk \
	unzip \
	wget \
	tzdata \
	&& locale-gen $LANG $LC_ALL && update-locale $LANG $LC_ALL \
	&& update-java-alternatives --set java-1.11.0-openjdk-amd64

RUN mkdir -p /root/.android/
RUN touch /root/.android/repositories.cfg

# Android SDK tools
RUN wget -O /tmp/android-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip && \
	mkdir -p /opt/android-sdk && \
	unzip -q -d /opt/android-sdk /tmp/android-tools.zip && \
	rm -f /tmp/android-tools.zip && \
	chown -R root:root /opt/android-sdk
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK=/opt/android-sdk
ENV PATH=${ANDROID_HOME}/cmdline-tools/bin:${ANDROID_HOME}/build-tools/32.0.0:${PATH}

# Android SDK libraries, NDK
ENV ANDROID_DEPS='platform-tools \
 build-tools;32.0.0 \
 platforms;android-32 \
 extras;android;m2repository \
 extras;google;m2repository'
RUN (while sleep 1; do echo "y"; done) | sdkmanager --sdk_root=${ANDROID_HOME} $ANDROID_DEPS

# Fastlane
RUN gem install fastlane bundler

VOLUME /home/builder/src
