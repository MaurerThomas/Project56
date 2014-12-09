# -*- coding: utf-8 -*-

# Scrapy settings for alternate project
#
# For simplicity, this file contains only the most important settings by
# default. All the other settings are documented here:
#
#     http://doc.scrapy.org/en/latest/topics/settings.html
#

BOT_NAME = 'alternate'

SPIDER_MODULES = ['alternate.spiders']
NEWSPIDER_MODULE = 'alternate.spiders'
DOWNLOAD_DELAY = 2
ITEM_PIPELINES = {
	'alternate.pipelines.AlternatePipeline' : 300,
}
