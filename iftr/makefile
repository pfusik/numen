include ../common.mak

.PHONY: run
run: iftr.obx
	$(RUNOBX) $<

.PHONY: java
java: Test.class
	$(JAVA) Test

iftr.nex iftr.obx: wirhilo.map statek.raw einsrays.raw hi.dat lo.dat sciema.asx

wirhilo.map hi.dat lo.dat: Test.class
	$(JAVA) Test write

Test.class: ../PCXImage.class

%.raw: %.pcx ../PCXtoGR9.class
	$(JAVA) PCXtoGR9 $< $@

sciema.asx: Sciema.class sciema
	$(JAVA) Sciema >sciema.asx

.PHONY: clean
clean:
	$(RM) iftr.nex iftr.obx
	$(RM) wirhilo.map hi.dat lo.dat Test*.class
	$(RM) statek.raw einsrays.raw
	$(RM) sciema.asx Sciema.class
