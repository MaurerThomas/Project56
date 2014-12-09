#coding: utf8 
from scrapy.spider import Spider
from scrapy.selector import Selector
from alternate.items import AlternateItem
import re

class spin_Spider(Spider):
	name = "spin"
	allowed_domains = ["alternate.nl"] 
	start_urls = [
						"https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=1000&bgid=8148&lk=9309&tk=7&navId=2436", #behuizingen
						"https://www.alternate.nl/html/product/listing.html?size=1000&lk=13472&tk=7&navId=20678", #DDR4
						"https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=1000&bgid=8296&lk=9326&tk=7&navId=11556", #DDR3
						"https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=1000&lk=9312&tk=7&navId=11554", #DDR2
						"https://www.alternate.nl/html/product/listing.html?navId=11542&tk=7&lk=9335", #DDR
						"https://www.alternate.nl/html/product/listing.html?navId=11558&tk=7&lk=9324", #SDRAM
						"https://www.alternate.nl/html/product/listing.html?navId=17362&tk=7&lk=9518", #Geluidskaarten PCI
						"https://www.alternate.nl/html/product/listing.html?navId=17363&tk=7&lk=9519", #Geluidskaarten PCIe
						"https://www.alternate.nl/html/product/listing.html?navId=17364&tk=7&lk=9520", #Geluidskaarten USB
						"https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=500&bgid=11369&lk=9374&tk=7&navId=11606",#grafische kaarten PCIe ATI/AMD
						"https://www.alternate.nl/html/product/listing.html?navId=1358&tk=7&lk=9372", #Grafische kaarten - PCIe Matrox
						"https://www.alternate.nl/html/product/listing.html?navId=1360&tk=7&lk=9381", #Grafische kaarten - AGP kaarten
						"https://www.alternate.nl/html/product/listing.html?navId=1362&tk=7&lk=9361", #Grafische kaarten - PCI kaarten
						"https://www.alternate.nl/html/product/listing.html?navId=749&tk=7&lk=9362", #Koeling - GPU koelers
						"https://www.alternate.nl/html/product/listing.html?filter_5=&filter_4=&filter_3=&filter_2=&filter_1=&size=500&bgid=8459&lk=9563&tk=7&navId=11584", #Harde schijven intern - SATA
						"https://www.alternate.nl/html/product/listing.html?navId=990&navId=988&tk=7&lk=9553", #Harde schijven intern - SAS - 2,5 inch
						"https://www.alternate.nl/html/product/listing.html?navId=17557&bgid=8459&tk=7&lk=9601", #Harde schijven intern - Hybride
						"https://www.alternate.nl/html/product/listing.html?navId=11890&bgid=8985&tk=7&lk=9585", #SSD - SATA
						"https://www.alternate.nl/html/product/listing.html?navId=14655&bgid=8985&tk=7&lk=9599", #SSD - mSATA
						"https://www.alternate.nl/html/product/listing.html?navId=19991&tk=7&lk=12801", #SSD - M.2
						"https://www.alternate.nl/html/product/listing.html?navId=1690&tk=7&lk=9590", #SSD - PCI Express
						"https://www.alternate.nl/html/product/listing.html?navId=11898&bgid=8215&tk=7&lk=9344", #Koeling - CPU koelers
						"https://www.alternate.nl/html/product/listing.html?navId=756&tk=7&lk=9359",#Koeling - HDD  koelers
						"https://www.alternate.nl/html/product/listing.html?navId=749&tk=7&lk=9345", #Koeling - GPU koelers
						"https://www.alternate.nl/html/product/listing.html?navId=11568&bgid=8215&tk=7&lk=9346", #Koeling - Behuizing koelers
						"https://www.alternate.nl/html/product/listing.html?navId=758&tk=7&lk=9350", #Koeling - Geheugen koelers
						"https://www.alternate.nl/html/product/listing.html?navId=11570&tk=7&lk=9351", #Koeling - Waterkoeling
						"https://www.alternate.nl/html/product/listing.html?navId=11622&tk=7&lk=9419", #Moederborden - AMD
						"https://www.alternate.nl/html/product/listing.html?navId=11626&tk=7&lk=9435",#Moederborden - Intel
						"https://www.alternate.nl/html/product/listing.html?navId=17480&tk=7&lk=9452", #Moederborden - Overige - Geïntegreerde CPU
						"https://www.alternate.nl/html/product/listing.html?navId=1392&tk=7&lk=9460", #Controllers - eSATA
						"https://www.alternate.nl/html/product/listing.html?navId=1396&tk=7&lk=9454", #Controllers - IDE
						"https://www.alternate.nl/html/product/listing.html?navId=1400&tk=7&lk=9459", #Controllers - SCSI
						"https://www.alternate.nl/html/product/listing.html?navId=1398&tk=7&lk=9455", #Controllers - SATA
						"https://www.alternate.nl/html/product/listing.html?navId=1402&tk=7&lk=9461", #Controllers - Serial Attached SCSI
						"https://www.alternate.nl/html/product/listing.html?navId=1404&tk=7&lk=9456", #Controllers - FireWire
						"https://www.alternate.nl/html/product/listing.html?navId=1406&tk=7&lk=9457", #Controllers - USB
						"https://www.alternate.nl/html/product/listing.html?navId=1450&tk=7&lk=9535", #Netwerkkaarten - PCI
						"https://www.alternate.nl/html/product/listing.html?navId=1452&tk=7&lk=9536", #Netwerkkaarten - PCIe
						"https://www.alternate.nl/html/product/listing.html?navId=1456&tk=7&lk=9537", #Netwerkkaarten - USB
						"https://www.alternate.nl/html/product/listing.html?navId=11572&bgid=10846&tk=7&lk=9487", #Processor - Desktop
						"https://www.alternate.nl/html/product/listing.html?navId=11898&tk=7&lk=9493", #Koeling - CPU koelers
						"https://www.alternate.nl/html/product/listing.html?navId=11604&bgid=8215&tk=7&lk=9533" #Voeding
                      ]
	def parse (self,response):
		
		products = response.xpath('//*[starts-with(@class,"listRow")]')
		items = []
		for product in products:
			item = AlternateItem()
			temp = product.xpath('a/span/span/h2/*[starts-with(@class,"name")]/span/text()').extract()
			item['site'] = "alternate"
			item['url'] = product.xpath('a/@href').extract()
			item['merk'] = temp[0]
			temp = temp[1].split(',')
			
			item['model'] = temp[0]
			item['type'] = product.xpath('//*[starts-with(@class,"seoListingHeadline")]/text()').extract()
			item['euro'] = product.xpath('div/p/*[starts-with(@class,"price right")]/text()').extract()
			item['euro'] = re.findall(r'\d+', ''.join(item['euro']))
			item['cent'] = product.xpath('div/p/span/sup/text()').extract()
			item['cent'] = re.findall(r'\d+', ''.join(item['cent']))
			items.append(item)			
		return items