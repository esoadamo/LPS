LockPicker Simulator
===================
Hra LockPicker Simulator je hra pøesnì o tom, co její název vypovídá. Byla vytvoøena pro programátorskou soutì [GeekWork 2016](http://www.itnetwork.cz/nezarazene/programatorska-soutez-geekwork-2016/hra-lock-picker-simulator) jako webová hra, nicménì díky pouití jazyka Java je plnì (a moná i lépe) hratelná jako desktopová aplikace.


----------
## Vlastnosti ##
 1. Dynamicky generované prostøedí
 2. Akènost - pøi maximálním FPS (pøedevším v offline reimu) se ze hry stává opravdu napínavı souboj s nepøítelem a jeho taktikou, kde rychlé reflexy mohou zvrátit celkovı vısledek
 3. Taktika - Hra nabízí monost peèlivì vymyslet svoji taktiku a rozdrtit nepøítele na plné èáøe
 4. Soutìivost - vyhrávejte/na­kupujte pomocí vyhrané mìny mnoho kosmetickıch vylepšení, které všem ukáí, e vy jste tady šéf
 5. Vysoké rozlišení - a 4K, nicménì FPS krapet trpí
 6. Open source - po ukonèení soutìe budou publikovány zdrojové kódy
 7. Web & Desktop klient - hrajte pohodlnì bez pøipojení k internetu, nebo si uívejte soupeøení s ostatními hráèi
 8. Nenároènı server (kadı si jej mùe rozjet s minimálními náklady)
 9. Pøátelskı programátorsko-designérskı tım ve sloení Adam Hlaváèek, Pavel Rùièka, Tomáš Hlaváèek
## Pøíbìh ##
###Historické pozadí
Sir A'Lock Pick zaèínal naprosto od píky. Brzy ale zjistil, e lidé rádi zamykají vìci, a tak zaèal vyrábìt zámky. Jeho zámky mìly takovou kvalitu, e netrvalo dlouho a jeho pùvodnì malı obchùdek se stal nejvìtším svìtovım obchodem se zámky. Bohuel taková vìc také vyvolala nechtìnou pozornost u rùznıch lapkù a zlodìjù, kteøí si chtìli tyto vysoce kvalitní zámky odnést bez placení.
###Vy
A proto vstupujete do hry vy - jste nezajímavı detektiv z nezajímavého mìsta v nìjakém nespecifikovaném státì, do kterého pøišly noviny s inzerátem: "*Nejvìtší obchodní dùm se zámky kvùli zvıšení krádeí hledá bezpeènostního zamìstance na plnı úvazek.*" Zaujalo Vás to a proto jste jel na pracovní pohovor, kde jste se dozvìdìl více o vašem úkolu: musíte pozorovat a odhadnout, kterı zámek se kriminální ivly pokusí ukrást a zabránit jim v tom. Není to lehkı úkol, zámkù je mnoho a vy jste jeden. Ale jste ten Jeden!
###Budocnoust
Budoucností se ji zabıvá [jinı projekt](https://github.com/esoadamo/LPSGM)
##Instalace
###Samotná desktopová aplikace
Z [releases ](https://github.com/esoadamo/LPS/releases) stáhnìte nejnovìjší *_client zip, rozbalte a spusì LPS.jar.
###Server
 Z [releases ](https://github.com/esoadamo/LPS/releases) stáhnìte nejnovìjší *_server zip, vytvoøte novou databázi lockpicker a importujte soubor lps.sql. Poté na svùj webovı server nahrajte lps.php (aplikace vyuívá pro komunikaci knihovnu [MySQL2PHP2Java](https://github.com/esoadamo/MySQL2PHP2Java)) a nastavte podle instrukcí v souboru. Tím jste zprovoznili svùj LPS server.
###Webová aplikace
Pro spuštìní webové aplikace se pouívá naprosto úasnı [Webswing](http://webswing.org/). Pøedpokládejme, e jste si stáhli nejnovìjší *_client zip a umístili jej do sloky LPS která je podsloukou Webswing serveru.
Pøíklad konfigurace Webswing:
`...
{
    "name" : "LPS",
    "icon" : "data/coin.png",
    "mainClass" : "game.aho.lps.LockPickerSimulator",
    "classPathEntries" : [ "LPS.jar" ],
    "vmArgs" : "-Xmx200m -DauthorizedUser=${user}",
    "args" : "--web-browser --no-fullscreen --resolution 4",
    "homeDir" : "LPS",
    "maxClients" : 8,
    "antiAliasText" : true,
    "swingSessionTimeout" : 300,
    "authorization" : false,
    "isolatedFs" : false,
    "debug" : false,
    "authentication" : false,
    "directdraw" : true,
    "allowDelete" : false,
    "allowDownload" : false,
    "allowUpload" : false,
    "allowJsLink" : true
  },
  ...`

