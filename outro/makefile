include ../common.mak

.PHONY: run
run: outro.com
	$(RUNXEX) $<

outro.com: ../ZipXex.class zipxb600.obx outro.xex
	$(JAVA) ZipXex zipxb600.obx $$b600 outro.xex outro.com

zipxb600.obx: ../zipxex.asx
	$(XASM) /o:$@ /d:inflate=$$b600 $<

outro.xex: outro.asx eed22c.fnt eed22c.siz ../mptplfox.asx siekacz.mpt outro.not
	$(XASM) /o:outro.xex outro.asx

outro.not: HTML2NOT.class eed22c.siz outro.html
	$(JAVA) HTML2NOT eed22c.siz outro.html outro.not

.PHONY: clean
clean:
	$(RM) outro.com zipxb600.obx outro.xex outro.not HTML2NOT.class
