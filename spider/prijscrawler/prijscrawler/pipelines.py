# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html
import sys
import MySQLdb
import hashlib
from scrapy.exceptions import DropItem
from scrapy.http import Request
from scrapy.utils.project import get_project_settings 

class PrijsPipeline(object):
	def __init__(self):
		settings = get_project_settings() 
		self.conn = MySQLdb.connect(user= settings['MYSQL_USER'], passwd= settings['MYSQL_PASS'], db= settings['MYSQL_DB'], host= settings['MYSQL_HOST'], charset='utf8', use_unicode=True)
		self.cursor = self.conn.cursor()

	def process_item(self, item, spider):
		try:
			if len(item['cent']) > 0:
				self.cursor.execute("""INSERT INTO prijs_verloop (url, euro, cent) VALUES (%s, %s, %s)""", ("".join(item['url']), "".join(item['euro']), "".join(item['cent'])))
			else:
				self.cursor.execute("""INSERT INTO prijs_verloop (url, euro, cent) VALUES (%s, %s, %s)""", ("".join(item['url']), "".join(item['euro']),'0'))
			self.cursor.execute("""INSERT INTO url_ean (url) VALUES (%s) ON DUPLICATE KEY UPDATE url=url""", ("".join(item['url'])))
				
			self.conn.commit()
		except MySQLdb.Error, e:
			print "Error %d: %s" % (e.args[0], e.args[1])


		return item
