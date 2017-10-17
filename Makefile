NUTCH_DIR=

default: build install_plugin

build:
	sbt compile package assembly

install_plugin:
	if [ -z ${NUTCH_DIR} ]; then echo run make NUTCH_DIR=/nutch/path && exit 1; fi
	cp conf/plugin.xml \
		${NUTCH_DIR}/plugins/parse-interactive/
	cp target/scala-2.12/InteractiveParser-assembly-0.0.2.jar \
		${NUTCH_DIR}/plugins/parse-interactive/parse-interactive.jar

