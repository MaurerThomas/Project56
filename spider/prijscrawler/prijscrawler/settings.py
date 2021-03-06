# -*- coding: utf-8 -*-

# Scrapy settings for alternate project
#
# For simplicity, this file contains only the most important settings by
# default. All the other settings are documented here:
#
#     http://doc.scrapy.org/en/latest/topics/settings.html
#

BOT_NAME = 'prijscrawler'

SPIDER_MODULES = ['prijscrawler.spiders']
NEWSPIDER_MODULE = 'prijscrawler.spiders'
DOWNLOAD_DELAY = 2
DOWNLOADER_STATS = False
ITEM_PIPELINES = {
	'prijscrawler.pipelines.PrijsPipeline' : 300,
}

MYSQL_DB 		=	'pcbuilder'
MYSQL_USER		= 	'pcbuilder'
MYSQL_PASS		=	'project'
MYSQL_HOST 	=	'127.0.0.1'
# Crawl responsibly by identifying yourself (and your website) on the user-agent
#USER_AGENT = 'prijs crawler (+145.24.222.237)'
