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

class AlternatePipeline(object):
	def __init__(self):
		self.conn = MySQLdb.connect(user='pcbuilder', passwd='project', db='pcbuilder', host='127.0.0.1', charset='utf8', use_unicode=True)
		self.cursor = self.conn.cursor()

	def process_item(self, item, spider):
		try:
			if len(item['cent']) >0:
				self.cursor.execute("""INSERT INTO prijs_verloop (url, euro, cent) VALUES (%s, %s, %s)""", (item['url'][0], item['euro'][0], item['cent'][0]))
			else:
				self.cursor.execute("""INSERT INTO prijs_verloop (url, euro, cent) VALUES (%s, %s, %s)""", (item['url'][0], item['euro'][0],'0'))
			self.conn.commit()

		except MySQLdb.Error, e:
			print "Error %d: %s" % (e.args[0], e.args[1])


		return item
