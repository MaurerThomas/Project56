# -*- coding: utf-8 -*-
import scrapy


class SpinSpider(scrapy.Spider):
    name = "spin"
    allowed_domains = ["alternate.nl"]
    start_urls = (
        'http://www.alternate.nl/',
    )

    def parse(self, response):
        pass
