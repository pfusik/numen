include ../common.mak

.PHONY: run
run: tunnel56.obx
	$(RUNOBX) $<

.PHONY: java
java: Test.class
	$(JAVA) Test

tunnel56.nex tunnel56.obx: kwadra15.lot kwadrati.lot
tunnel56.obx: tabc.dat taba.dat tabr.dat

tabc.nex: tabc.asx tabc.dat

tabr.dat taba.dat tabc.dat: Test.class toruseru.dat
	$(JAVA) Test write

.PHONY: clean
clean:
	$(RM) tabc.nex tunnel56.nex tunnel56.obx
	$(RM) taba.dat tabc.dat tabr.dat Test*.class
