PLUGIN_VERSION=0.0.1
PLUGIN_ID=osisoft-pi-dialect

plugin:
	ant
	cat plugin.json|json_pp > /dev/null
	rm -rf dist
	mkdir dist
	zip -r dist/dss-plugin-${PLUGIN_ID}-${PLUGIN_VERSION}.zip plugin.json java-lib java-dialects