include ../common.mak

.PHONY: run
run: start.com
	$(RUNXEX) start.com

start.com: tests.xex start.zix
	cat $^ > start.com

tests.xex: tests.asx
	$(XASM) /o:$@ $<

start.zix: ../ZipXex.class zipx9600.obx start.xex
	$(JAVA) ZipXex zipx9600.obx $$9600 start.xex start.zix

zipx9600.obx: ../zipxex.asx
	$(XASM) /o:$@ /d:inflate=$$9600 $<

start.xex: start.asx ../filelen.asx font.raw diablik.gr8 numen.gr8
	$(XASM) /o:$@ $<

.PHONY: ../filelen.asx
../filelen.asx:
	$(MAKE) -C .. filelen.asx

font.raw: FontConv.class font.pcx
	$(JAVA) FontConv font.pcx font.raw

FontConv.class: ../PCXImage.class

diablik.gr8: HRConv.class diablik.pcx
	$(JAVA) HRConv diablik.pcx diablik.gr8

HRConv.class: ../PCXImage.class

numen.gr8: ../PCXtoGR8.class numen.pcx
	$(JAVA) PCXtoGR8 numen.pcx numen.gr8

.PHONY: clean
clean:
	$(RM) start.com tests.xex start.zix zipx9600.obx start.xex
	$(RM) font.raw diablik.gr8 numen.gr8
	$(RM) FontConv.class HRConv.class
