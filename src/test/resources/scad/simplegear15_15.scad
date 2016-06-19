// Helical Gears
// Catarina Mota
// catarinamfmota@gmail.com
// 20091123


//GEAR PARAMETERS
doubleHelical=1; //int: 0=no, 1=yes (will make the gear 2x taller)

gearHeight=5; //gear depth

pitchDiam=45; //pitch diameter

shaftDiam=5; //shaft diameter

//TEETH PARAMETERS
teethNum=10; //number of teeth (int)

addendum=2;
$fn = 20;

gearRadius = 15;
gearThickness = 3;
toothCount = 15;

spurGearApproximation(gearRadius, gearThickness, toothCount);

module spurGearApproximation(gearRadius, gearThickness, toothCount){
    echo("Usage: spurGearApproximation(gearRadius, gearThickness, toothCount);");
    echo("Example: spurGearApproximation(10,3,20);");
    circumference = 2*3.14159*gearRadius;
    toothWidth = circumference  / 2 / toothCount;
    toothLength = 2*toothWidth;
    for(i = [0:360/toothCount:360-(360/toothCount)]){
        spurTooth(toothWidth, toothLength, gearThickness, gearRadius, i);
    }
    cylinder(gearThickness, gearRadius, gearRadius, center = true);
}

module spurTooth(toothWidth, toothLength, toothDepth, gearRadius, rotation){
    rotate([0,0,rotation]){
        translate([0,gearRadius,0]){
            intersection(){
                translate([-(gearRadius-toothWidth/2), 0, 0]){
                    cylinder(toothDepth, gearRadius, gearRadius, center = true);
                }
                translate([(gearRadius-toothWidth/2), 0, 0]){
                    cylinder(toothDepth, gearRadius, gearRadius, center = true);
                }
                translate([0, 0, 0]){
                    cube([toothWidth, toothLength/1, toothDepth], center = true);
                }
            }
        }
    }
}