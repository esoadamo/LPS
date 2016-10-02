LockPicker Simulator
===================
Hra LockPicker Simulator je hra přesně o tom, co její název vypovídá. Byla vytvořena pro programátorskou soutěž [GeekWork 2016](http://www.itnetwork.cz/nezarazene/programatorska-soutez-geekwork-2016/hra-lock-picker-simulator) jako webová hra, nicméně díky použití jazyka Java je plně (a možná i lépe) hratelná jako desktopová aplikace.


----------
## Vlastnosti ##
 1. Dynamicky generované prostředí
 2. Akčnost - při maximálním FPS (především v offline režimu) se ze hry stává opravdu napínavý souboj s nepřítelem a jeho taktikou, kde rychlé reflexy mohou zvrátit celkový výsledek
 3. Taktika - Hra nabízí možnost pečlivě vymyslet svoji taktiku a rozdrtit nepřítele na plné čáře
 4. Soutěživost - vyhrávejte/na­kupujte pomocí vyhrané měny mnoho kosmetických vylepšení, které všem ukáží, že vy jste tady šéf
 5. Vysoké rozlišení - až 4K, nicméně FPS krapet trpí
 6. Open source - po ukončení soutěže budou publikovány zdrojové kódy
 7. Web & Desktop klient - hrajte pohodlně bez připojení k internetu, nebo si užívejte soupeření s ostatními hráči
 8. Nenáročný server (každý si jej může rozjet s minimálními náklady)
 9. Přátelský programátorsko-designérský tým ve složení Adam Hlaváček, Pavel Růžička, Tomáš Hlaváček
 
## Příběh ##

###Historické pozadí
Sir A'Lock Pick začínal naprosto od píky. Brzy ale zjistil, že lidé rádi zamykají věci, a tak začal vyrábět zámky. Jeho zámky měly takovou kvalitu, že netrvalo dlouho a jeho původně malý obchůdek se stal největším světovým obchodem se zámky. Bohužel taková věc také vyvolala nechtěnou pozornost u různých lapků a zlodějů, kteří si chtěli tyto vysoce kvalitní zámky odnést bez placení.
###Vy
A proto vstupujete do hry vy - jste nezajímavý detektiv z nezajímavého města v nějakém nespecifikovaném státě, do kterého přišly noviny s inzerátem: "*Největší obchodní dům se zámky kvůli zvýšení krádeží hledá bezpečnostního zaměstance na plný úvazek.*" Zaujalo Vás to a proto jste jel na pracovní pohovor, kde jste se dozvěděl více o vašem úkolu: musíte pozorovat a odhadnout, který zámek se kriminální živly pokusí ukrást a zabránit jim v tom. Není to lehký úkol, zámků je mnoho a vy jste jeden. Ale jste ten Jeden!
###Budocnoust
Budoucností se již zabývá [jiný projekt](https://github.com/esoadamo/LPSGM)
##Instalace
###Samotná desktopová aplikace
Z [releases ](https://github.com/esoadamo/LPS/releases) stáhněte nejnovější *_client zip, rozbalte a spusťě LPS.jar.
###Server
 Z [releases ](https://github.com/esoadamo/LPS/releases) stáhněte nejnovější *_server zip, vytvořte novou databázi lockpicker a importujte soubor lps.sql. Poté na svůj webový server nahrajte lps.php (aplikace využívá pro komunikaci knihovnu [MySQL2PHP2Java](https://github.com/esoadamo/MySQL2PHP2Java)) a nastavte podle instrukcí v souboru. Tím jste zprovoznili svůj LPS server.
###Webová aplikace
Pro spuštění webové aplikace se používá naprosto úžasný [Webswing](http://webswing.org/). Předpokládejme, že jste si stáhli nejnovější *_client zip a umístili jej do složky LPS která je podsloužkou Webswing serveru.
Příklad konfigurace Webswing:
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

