MAKEFLAGS = --no-print-directory --warn-undefined-variables

include common.mak

DEMOPARTS = \
	flash/flash.nex flash/tunnelz.ang flash/tunnelz.ray \
	duke/1intro/duke.nex \
	title/title.nex \
	duke/2corrid/duke.nex \
	tunnel56/tabc.nex tunnel56/tunnel56.nex \
	env/env.nex \
	babka/babka.nex \
	hipbump/hipbump.nex hipbump/bumpmap.gfx \
	duke/3window/duke.nex \
	tunnel34/tunnel34.nex tunnel34/tun_tabs.dat \
	rzoom/rzoom.nex \
	duke/4exit/duke.nex \
	iftr/iftr.nex iftr/statek.raw iftr/einsrays.raw \
	plasma2/plasma2.nex \
	duke/5pool/duke.nex \
	tiplazma/tiplazma.nex tiplazma/tiplazma.agf \
	logo256/logo256.nex logo256/logo256.gfx \
	duke/6box/duke.nex \
	tunnel46/tunnel46.nex \
	musk/musk.nex \
	rubik/rubik.nex \
	mocap/mocap.nex \
	tvectys/tvcalc.obx tvectys/tvdisp.nex \
	duke/7credits/duke.nex \
	bigbump/bigbump.nex
#	hiplazma/hiplazma.nex hiplazma/hiplazma.msk hiplazma/hiplazma.gfx hiplazma/hiplazma.ray
#	logo/logo.nex logo/logo.gfx

.PHONY: runxex
runxex: demo.xex
	$(RUNXEX) demo.xex

.PHONY: all
all: Numen.atr Numen_A.atr Numen_B.atr

.PHONY: runatrs
runatrs: Numen_A.atr Numen_B.atr
	$(RUNATR) Numen_A.atr

.PHONY: runatr
runatr: Numen.atr
	$(RUNATR) Numen.atr

Numen_A.atr: FilesToATR.class dos128.obx start/start.com memory.cfg numen1.tqa outro/outro.com numen.txt
	$(JAVA) FilesToATR 1040 128 Numen_A.atr dos128.obx start/start.com memory.cfg numen1.tqa outro/outro.com numen.txt

Numen_B.atr: FilesToATR.class bootb.obx numen2.tqa
	$(JAVA) FilesToATR 1040 128 Numen_B.atr bootb.obx numen2.tqa

Numen.atr: FilesToATR.class dos256.obx start/start.com memory.cfg numen1.tqa numen2.tqa outro/outro.com numen.txt
	$(JAVA) FilesToATR 376 256 Numen.atr dos256.obx start/start.com memory.cfg numen1.tqa numen2.tqa outro/outro.com numen.txt

demo.xex: numen1.tqa numen2.tqa
	cat numen1.tqa numen2.tqa > demo.xex

dos128.obx: dos.asx
	$(XASM) /o:dos128.obx /d:SECTOR_SIZE=128 dos.asx

dos256.obx: dos.asx
	$(XASM) /o:dos256.obx /d:SECTOR_SIZE=256 dos.asx

numen1.tqa numen2.tqa: DemoLinker.class loader.obx playinf.obx inflate.obx $(DEMOPARTS)
	$(JAVA) DemoLinker

loader.obx: banks.asx
loader.obx playinf.obx inflate.obx: numendef.asx

.PHONY: $(DEMOPARTS) start/start.com outro/outro.com
$(DEMOPARTS) start/start.com outro/outro.com:
	$(MAKE) -C $(@D) $(@F)

playinf.obx: bonoxxx.mpf mptplfox.asx

# filelen.asx is for start.com
filelen.asx: FileLength.class demo.xex outro/outro.com
#	echo "DEMO_LENGTH_PERCENT equ [%+99]/100" | $(JAVA) FileLength demo.xex > filelen.asx
#	echo "OUTRO_LENGTH_PERCENT equ [%+99]/100" | $(JAVA) FileLength outro/outro.com >> filelen.asx
	echo DEMO_LENGTH_PERCENT equ %/100 | $(JAVA) FileLength demo.xex > filelen.asx
	echo OUTRO_LENGTH_PERCENT equ %/100 | $(JAVA) FileLength outro/outro.com >> filelen.asx

numen.txt: AsciiToAtascii.class numen.txw
	$(JAVA) AsciiToAtascii numen.txw numen.txt

PCXtoGR8.class PCXtoGR9.class PCXtoHIP.class PCXGRBto256.class: PCXImage.class

.PHONY: clean
clean:
	$(RM) Numen.atr Numen_A.atr Numen_B.atr
	$(RM) FilesToATR.class dos128.obx dos256.obx bootb.obx
	$(RM) numen.txt AsciiToAtascii.class
	$(RM) demo.xex numen1.tqa numen2.tqa DemoLinker.class
	$(RM) filelen.asx FileLength.class ZipXex.class
	$(RM) loader.obx playinf.obx inflate.obx
	$(RM) PCXtoGR8.class PCXtoGR9.class
	$(RM) PCXtoHIP.class PCXGRBto256.class PCXImage.class
	$(MAKE) -C .util clean
	$(MAKE) -C start clean
	$(MAKE) -C outro clean
	$(MAKE) -C flash clean
	$(MAKE) -C duke clean
	$(MAKE) -C title clean
	$(MAKE) -C tunnel56 clean
	$(MAKE) -C env clean
	$(MAKE) -C babka clean
	$(MAKE) -C hipbump clean
	$(MAKE) -C tunnel34 clean
	$(MAKE) -C rzoom clean
	$(MAKE) -C iftr clean
	$(MAKE) -C plasma2 clean
#	$(MAKE) -C hiplazma clean
	$(MAKE) -C tiplazma clean
#	$(MAKE) -C logo clean
	$(MAKE) -C logo256 clean
	$(MAKE) -C tunnel46 clean
	$(MAKE) -C musk clean
	$(MAKE) -C rubik clean
	$(MAKE) -C mocap clean
	$(MAKE) -C tvectys clean
	$(MAKE) -C bigbump clean
