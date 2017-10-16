NUTCH_DIR=

default: build install_plugin

build:
	sbt compile package assembly

install_plugin:
	if [ -z ${NUTCH_DIR} ]; then \
		echo "set NUTCH_DIR in Makefile or in cmd line `make NUTCH_DIR=....`" && exit 1; \
	fi
	cp target/scala-2.12/InteractiveParser-assembly-0.0.2.jar \
		${NUTCH_DIR}/plugins/parse-interactive/parse-interactive.jar

