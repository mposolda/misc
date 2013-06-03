// jQuery part
$(document).ready(function(){
    $("#odstavec").text("Hello world!");

    //$(".cervena").toggle(2000);
    //$(".cervena").toggle(2000);
    //$(".modra").toggle(2000);

    // vybere všechny prvky typu span
    //$("#text span").toggle(2000);

    // vybere první span, který se nachází v #text
    //$("#text span:first").toggle(2000);

    // vybere poslední span, který se nachází v #text
    //$("#text span:last").toggle(2000);

    // vybere sudé spany, které se nachází v #text
    $("#text span:odd").toggle(2000);

    // Vsechny
    $("tr:gt(2)").css("background-color", "red");
    // $("tr:odd").css("background-color", "black");

    // podtrhne všechny nadpisy
    $(":header").css("text-decoration", "underline");

    // Vybrani prvku pomoci funkce. Podtrhne to prvek is ID "target" pokud prvek s ID "determinator" obsahuje hodnotu "text"
    $("#target").filter(function() {
        if ($("#determinator").text() == "text")
        {
            return true;
        }
        else
        {
           return false;
        }
    }).css("text-decoration", "underline");

    // s vybranými prvky můžeme samozřejmě provést nějakou akci,
    $("div:contains('Text, ktery ma vybrany prvek obsahovat')").css("text-decoration", "underline");

    // Vybere spany pod divem s ID="test2", ktery obsahuji podelement "a"
    $("#test2 span:has(a)").css("text-decoration", "underline");

    // Treti potomek divu s ID="test2"
    $("#test2 :nth-child(3)").css("color", "red");;

    // Vybirani inputu, ktere jsou zaskrtnuty
    // $("input:checked");

    // Podminka, ktera vybere div s class='gold' protoze zacina na 'g' a zaroven konci na 'ld'
    $("div[class ^= 'g'][class $= 'ld']").css("color", "blue");

    // Traversing -
    $("#traversing-example1").children().css("color", "cyan");
    $("#traversing-example1 #trv-span1").siblings().css("color", "green");

    $("#traversing-example2").click(function(event) {
         // pri kliknuti na seznam (i prvky v nem). Kdyz to bude "li"
         if ($(event.target).is('li') ) {
             // tak mu nastavi barvu pozadi na cervenou
             $(event.target).css('background-color', 'red');
         } else if ($(event.target).is('strong') ) {
             $(event.target).css('background-color', 'blue');
         } else if ($(event.target).is('span') ) {
             $(event.target).css('background-color', 'green');
         }
    });

    // Carka vybere vsechny
    $("#lastpart1, #lastpart2").css("background-color", "green");

    // Ale nevim, jak se z parenta dostat k podelementum (treba ke vsem spanum)
    $("#lastpart3").parent().css("background-color", "blue");


    // Workaround je tohle. Chci zbarvit zelene prvni 'span' ktery je sibling od divu s ID="lastpart3"
    $("#lastpart3 ~ span:first").css("background-color", "green");
});