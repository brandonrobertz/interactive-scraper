NUTCH_DIR=

default: build install

build:
	sbt compile package assembly

check_install_arg:
	if [ -z ${NUTCH_DIR} ]; then echo run make NUTCH_DIR=/nutch/path && exit 1; fi

install: check_install_arg install_parse_plugin install_protocol_plugin

install_parse_plugin: check_install_arg
	mkdir -p ../plugins/parse-interactive
	cp conf/parse-interactive/plugin.xml \
		${NUTCH_DIR}/plugins/parse-interactive/
	cp target/scala-2.12/InteractiveParser-assembly-0.0.2.jar \
		${NUTCH_DIR}/plugins/parse-interactive/parse-interactive.jar

install_protocol_plugin: check_install_arg
	mkdir -p ../plugins/protocol-interactivehttp
	cp conf/protocol-interactivehttp/plugin.xml \
		${NUTCH_DIR}/plugins/protocol-interactivehttp/
	cp target/scala-2.12/InteractiveParser-assembly-0.0.2.jar \
		${NUTCH_DIR}/plugins/protocol-interactivehttp/protocol-interactivehttp.jar

