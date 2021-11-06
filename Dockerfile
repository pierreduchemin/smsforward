FROM ubuntu:20.04

ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8
ENV TZ=Etc/UTC

RUN dpkg --add-architecture i386
RUN apt-get update
RUN DEBIAN_FRONTEND="noninteractive" apt-get -y install tzdata
RUN apt-get install -y --no-install-recommends \
    build-essential \
    ruby2.7-dev \
	git \
	libsdl1.2debian:i386 \
	locales \
	openjdk-8-jdk \
	unzip \
	wget \
	&& locale-gen $LANG $LC_ALL && update-locale $LANG $LC_ALL \
	&& update-java-alternatives --set java-1.8.0-openjdk-amd64

# Fastlane
RUN gem install fastlane bundler

RUN mkdir -p /root/.android/
RUN touch /root/.android/repositories.cfg

# Android SDK tools
RUN wget -O /tmp/android-tools.zip https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip && \
	mkdir -p /opt/android-sdk && \
	unzip -q -d /opt/android-sdk /tmp/android-tools.zip && \
	rm -f /tmp/android-tools.zip && \
	chown -R root:root /opt/android-sdk
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK=/opt/android-sdk
ENV PATH=${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/build-tools/31.0.0:${PATH}

# Android SDK libraries, NDK
ENV ANDROID_DEPS='platform-tools \
 build-tools;31.0.0 \
 platforms;android-31 \
 extras;android;m2repository \
 extras;google;m2repository'
RUN (while sleep 1; do echo "y"; done) | sdkmanager $ANDROID_DEPS

VOLUME /home/builder/src
