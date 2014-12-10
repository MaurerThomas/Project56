# -*- coding: utf-8 -*-

# Scrapy settings for specscrawl project
#
# For simplicity, this file contains only the most important settings by
# default. All the other settings are documented here:
#
#     http://doc.scrapy.org/en/latest/topics/settings.html
#

BOT_NAME = 'specscrawl'

SPIDER_MODULES = ['specscrawl.spiders']
NEWSPIDER_MODULE = 'specscrawl.spiders'
DOWNLOAD_DELAY = 1
# MongoDB pipeline configuration
ITEM_PIPELINES = [
  'scrapy_mongodb.MongoDBPipeline',
]

MONGODB_URI = 'mongodb://localhost:27017'
MONGODB_DATABASE = 'mydb'
MONGODB_COLLECTION = 'mydb'
# Data buffering
MONGODB_BUFFER_DATA = 10
# Timestamp
MONGODB_ADD_TIMESTAMP = True
# Crawl responsibly by identifying yourself (and your website) on the user-agent
#USER_AGENT = 'specs crawler (+145.24.222.119)'
