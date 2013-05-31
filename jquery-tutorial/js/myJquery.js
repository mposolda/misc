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
});