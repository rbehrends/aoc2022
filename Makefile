GRADLE_OPTS=-Xmx2G
GRADLE=./gradlew
all:
	$(GRADLE) build
run%:
	$(GRADLE) $@
test%:
	$(GRADLE) $@
fetch%:
	$(GRADLE) $@
native%:
	$(GRADLE) $@
stop:
	$(GRADLE) --stop
clean:
	$(GRADLE) clean
