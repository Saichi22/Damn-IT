package data;

import java.util.ArrayList;
import model.Product;

/*
 * Hard-coded product list (no database or file needed).
 * Generated from the shared uniqlo_products.json, then given an id,
 * category, section (MEN / WOMEN / KIDS / IT), price in PHP and stock.
 * Edit any line to change a product.
 */
public final class ProductCatalog {

    private ProductCatalog() { }

    public static ArrayList<Product> create() {
        ArrayList<Product> list = new ArrayList<>();
        list.add(new Product("UQ001", "Zip Up Short Jacket", "Outerwear", "MEN", 1990.0, 24,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/487516/item/phgoods_30_487516_3x4.jpg"));
        list.add(new Product("UQ002", "Crew Neck Short Sleeve T-Shirt", "T-Shirts", "MEN", 590.0, 18,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/424873/item/phgoods_15_424873_3x4.jpg"));
        list.add(new Product("UQ003", "Cotton Stand Collar Shirt", "Shirts & Blouses", "MEN", 1290.0, 30,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/489425/item/phgoods_01_489425_3x4.jpg"));
        list.add(new Product("UQ004", "UV Protection Crew Neck Sweater | Short Sleeve", "Knitwear", "MEN", 1490.0, 12,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/474983/item/phgoods_10_474983_3x4.jpg"));
        list.add(new Product("UQ005", "EZY Barrel Jeans", "Jeans", "WOMEN", 1990.0, 40,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488630/item/phgoods_63_488630_3x4.jpg"));
        list.add(new Product("UQ006", "AIRism Cotton Short Sleeve T Dress", "Dresses", "WOMEN", 1490.0, 8,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/485733/item/phgoods_16_485733_3x4.jpg"));
        list.add(new Product("UQ007", "Cotton Relaxed Ankle Pants", "Pants", "WOMEN", 1290.0, 15,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/482243/item/goods_18_482243_3x4.jpg"));
        list.add(new Product("UQ008", "Wireless Bra 3D Hold", "Innerwear", "WOMEN", 490.0, 3,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/482659/item/phgoods_11_482659_3x4.jpg"));
        list.add(new Product("UQ009", "Round Mini Shoulder Bag", "Bags", "IT", 490.0, 5,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/481610/item/goods_10_481610_3x4.jpg"));
        list.add(new Product("UQ010", "Windproof Stand Blouson", "Outerwear", "MEN", 2490.0, 35,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/486167/item/phgoods_32_486167_3x4.jpg"));
        list.add(new Product("UQ011", "DRY-EX Polo Shirt", "Polos", "MEN", 890.0, 24,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/482305/item/phgoods_53_482305_3x4.jpg"));
        list.add(new Product("UQ012", "3D Knit V Neck Sweater Striped", "Knitwear", "MEN", 1990.0, 18,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/492577/item/phgoods_37_492577_3x4.jpg"));
        list.add(new Product("UQ013", "Stretch Easy Shorts Denim", "Shorts & Skorts", "MEN", 790.0, 30,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/481583/item/phgoods_64_481583_3x4.jpg"));
        list.add(new Product("UQ014", "AIRism Seamless Boxer Briefs", "Innerwear", "MEN", 790.0, 0,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/482556/item/goods_58_482556_3x4.jpg"));
        list.add(new Product("UQ015", "Utility Backpack", "Bags", "IT", 790.0, 40,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/488858/item/goods_56_488858_3x4.jpg"));
        list.add(new Product("UQ016", "Fluffy Yarn Fleece Full-Zip Jacket", "Outerwear", "MEN", 2990.0, 8,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/478168/item/phgoods_10_478168_3x4.jpg"));
        list.add(new Product("UQ017", "Cotton Color Crew Neck T-Shirt", "T-Shirts", "MEN", 790.0, 15,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/483432/item/phgoods_10_483432_3x4.jpg"));
        list.add(new Product("UQ018", "Easy Shorts (Twill)", "Shorts & Skorts", "MEN", 990.0, 22,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/465112/item/phgoods_31_465112_3x4.jpg"));
        list.add(new Product("UQ019", "Cotton Mesh Bodysuit Sleeveless", "Innerwear", "WOMEN", 1090.0, 5,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/481764/item/goods_12_481764_3x4.jpg"));
        list.add(new Product("UQ020", "BABY Toddler PUFFTECH Washable Parka", "Outerwear", "KIDS", 2390.0, 35,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/486416/item/goods_11_486416_3x4.jpg"));
        list.add(new Product("UQ021", "Mini T-Shirt Long Sleeve", "T-Shirts", "WOMEN", 990.0, 24,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/487579/item/phgoods_10_487579_3x4.jpg"));
        list.add(new Product("UQ022", "Cotton Volume Sleeve Shirt", "Shirts & Blouses", "WOMEN", 1490.0, 18,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488560/item/phgoods_39_488560_3x4.jpg"));
        list.add(new Product("UQ023", "EZY Wide Straight Jeans", "Jeans", "WOMEN", 2490.0, 30,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/484814/item/phgoods_64_484814_3x4.jpg"));
        list.add(new Product("UQ024", "Cotton Blend Volume Sleeve Dress", "Dresses", "WOMEN", 1990.0, 12,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/487279/item/phgoods_34_487279_3x4.jpg"));
        list.add(new Product("UQ025", "Cotton Relaxed Ankle Pants Denim", "Pants", "WOMEN", 1490.0, 40,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/483896/item/phgoods_65_483896_3x4.jpg"));
        list.add(new Product("UQ026", "Utility Shoulder Bag", "Bags", "IT", 1290.0, 8,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/488859/item/goods_57_488859_3x4.jpg"));
        list.add(new Product("UQ027", "DRY-EX UV Protection Full-Zip Hoodie", "Outerwear", "MEN", 2490.0, 15,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/465203/item/phgoods_18_465203_3x4.jpg"));
        list.add(new Product("UQ028", "AIRism Cotton Oversized Crew Neck T-Shirt", "T-Shirts", "MEN", 590.0, 22,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/465185/item/phgoods_11_465185_3x4.jpg"));
        list.add(new Product("UQ029", "3D Knit V Neck Sweater", "Knitwear", "MEN", 2490.0, 5,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/491361/item/phgoods_49_491361_3x4.jpg"));
        list.add(new Product("UQ030", "Stretch Easy Shorts", "Shorts & Skorts", "MEN", 1290.0, 2,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/481582/item/phgoods_31_481582_3x4.jpg"));
        list.add(new Product("UQ031", "2 Way One Handle Bag", "Bags", "IT", 490.0, 24,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/486771/item/goods_32_486771_3x4.jpg"));
        list.add(new Product("UQ032", "Fleece Full-Zip Jacket", "Outerwear", "MEN", 2990.0, 18,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/486660/item/phgoods_27_486660_3x4.jpg"));
        list.add(new Product("UQ033", "AIRism Cotton Graphic Crew Neck T-Shirt", "T-Shirts", "MEN", 790.0, 30,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/489218/item/phgoods_01_489218_3x4.jpg"));
        list.add(new Product("UQ034", "Geared Shorts", "Shorts & Skorts", "MEN", 790.0, 12,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/483406/item/phgoods_30_483406_3x4.jpg"));
        list.add(new Product("UQ035", "Zip Up Short Jacket Denim", "Outerwear", "MEN", 1990.0, 40,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488787/item/phgoods_69_488787_3x4.jpg"));
        list.add(new Product("UQ036", "UV Protection Crew Neck Long Sleeve Cardigan", "Knitwear", "MEN", 1490.0, 8,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/465484/item/phgoods_15_465484_3x4.jpg"));
        list.add(new Product("UQ037", "EZY Wide Straight Jeans | Longer Length", "Jeans", "WOMEN", 2990.0, 15,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/482281/item/phgoods_64_482281_3x4.jpg"));
        list.add(new Product("UQ038", "Seersucker Shirt Dress | Short Sleeve", "Dresses", "WOMEN", 2490.0, 22,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/484062/item/phgoods_57_484062_3x4.jpg"));
        list.add(new Product("UQ039", "Cotton Volume Easy Pants", "Pants", "WOMEN", 1990.0, 5,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/487589/item/phgoods_11_487589_3x4.jpg"));
        list.add(new Product("UQ040", "Relax Wireless Bra", "Innerwear", "WOMEN", 490.0, 35,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/482662/item/phgoods_21_482662_3x4.jpg"));
        list.add(new Product("UQ041", "Quilted Puffy Shoulder Bag", "Bags", "IT", 790.0, 24,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/486459/item/goods_30_486459_3x4.jpg"));
        list.add(new Product("UQ042", "DRY-EX UV Protection Hoodie", "Outerwear", "MEN", 2490.0, 18,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/483975/item/phgoods_69_483975_3x4.jpg"));
        list.add(new Product("UQ043", "AIRism Cotton Crew Neck Long Sleeve T-Shirt", "T-Shirts", "MEN", 990.0, 30,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/465193/item/phgoods_16_465193_3x4.jpg"));
        list.add(new Product("UQ044", "3D Knit Boat Neck Sweater", "Knitwear", "WOMEN", 1990.0, 12,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/491362/item/phgoods_67_491362_3x4.jpg"));
        list.add(new Product("UQ045", "Stretch Easy Shorts (Seersucker)", "Shorts & Skorts", "WOMEN", 990.0, 40,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/458325/item/phgoods_64_458325_3x4.jpg"));
        list.add(new Product("UQ046", "BABY Newborn Bodysuit Short Sleeve", "Innerwear", "KIDS", 490.0, 8,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/486345/item/goods_10_486345_3x4.jpg"));
        list.add(new Product("UQ047", "BABY Toddler Fluffy Yarn Fleece Full-Zip Jacket", "Outerwear", "KIDS", 1790.0, 15,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/486444/item/goods_11_486444_3x4.jpg"));
        list.add(new Product("UQ048", "Mini T-Shirt", "T-Shirts", "WOMEN", 590.0, 0,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/487277/item/phgoods_10_487277_3x4.jpg"));
        list.add(new Product("UQ049", "UV Protection Crew Neck Cardigan", "Knitwear", "MEN", 2490.0, 5,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/484505/item/phgoods_35_484505_3x4.jpg"));
        list.add(new Product("UQ050", "EZY Jeans", "Jeans", "MEN", 1990.0, 35,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/455476/item/phgoods_32_455476_3x4.jpg"));
        list.add(new Product("UQ051", "Ultra Stretch AIRism Sleeveless Dress", "Dresses", "WOMEN", 1490.0, 24,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/484883/item/phgoods_39_484883_3x4.jpg"));
        list.add(new Product("UQ052", "Active Wireless Bra", "Innerwear", "WOMEN", 1090.0, 18,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/487016/item/phgoods_19_487016_3x4.jpg"));
        list.add(new Product("UQ053", "Shoulder Bag", "Bags", "IT", 1290.0, 30,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/488439/item/goods_57_488439_3x4.jpg"));
        list.add(new Product("UQ054", "AIRism Cotton Striped T-Shirt", "T-Shirts", "MEN", 790.0, 12,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/486103/item/phgoods_76_486103_3x4.jpg"));
        list.add(new Product("UQ055", "DRY-EX Half-Zip Polo Shirt", "Polos", "MEN", 990.0, 40,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/482306/item/goods_55_482306_3x4.jpg"));
        list.add(new Product("UQ056", "Souffle Yarn Crew Neck Sweater", "Knitwear", "WOMEN", 1490.0, 8,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/486080/item/phgoods_34_486080_3x4.jpg"));
        list.add(new Product("UQ057", "Ultra Stretch Active Shorts", "Shorts & Skorts", "MEN", 1290.0, 15,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/475386/item/phgoods_19_475386_3x4.jpg"));
        list.add(new Product("UQ058", "Ultra Stretch Sweat Shorts", "Shorts & Skorts", "MEN", 790.0, 22,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/485856/item/phgoods_58_485856_3x4.jpg"));
        list.add(new Product("UQ059", "PUFFTECH Washable Parka", "Outerwear", "MEN", 3990.0, 5,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/491181/item/phgoods_10_491181_3x4.jpg"));
        list.add(new Product("UQ060", "AIRism Cotton Crew Neck T-Shirt", "T-Shirts", "MEN", 990.0, 35,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/474592/item/phgoods_11_474592_3x4.jpg"));
        list.add(new Product("UQ061", "GIRLS Easy Shorts", "Shorts & Skorts", "KIDS", 590.0, 24,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/483395/item/phgoods_10_483395_3x4.jpg"));
        list.add(new Product("UQ062", "Rib Mini T-Shirt", "T-Shirts", "WOMEN", 590.0, 18,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/484457/item/phgoods_10_484457_3x4.jpg"));
        list.add(new Product("UQ063", "Cotton Dobby V Neck Blouse Short Sleeve", "Shirts & Blouses", "WOMEN", 1790.0, 30,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/483881/item/phgoods_60_483881_3x4.jpg"));
        list.add(new Product("UQ064", "EZY Denim Relaxed Pants", "Pants", "MEN", 1290.0, 12,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/494543/item/phgoods_67_494543_3x4.jpg"));
        list.add(new Product("UQ065", "Linen Blend Sleeveless Mini Dress", "Dresses", "WOMEN", 1990.0, 40,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/482795/item/phgoods_17_482795_3x4.jpg"));
        list.add(new Product("UQ066", "Linen Blend Easy Pants", "Pants", "WOMEN", 1490.0, 8,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/478528/item/phgoods_30_478528_3x4.jpg"));
        list.add(new Product("UQ067", "Cotton Wireless Bra", "Innerwear", "WOMEN", 490.0, 15,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488145/item/phgoods_54_488145_3x4.jpg"));
        list.add(new Product("UQ068", "Mock Neck T-Shirt Long Sleeve", "T-Shirts", "MEN", 790.0, 22,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488605/item/phgoods_35_488605_3x4.jpg"));
        list.add(new Product("UQ069", "3D Knit Polo Sweater", "Knitwear", "MEN", 1990.0, 5,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/491360/item/phgoods_67_491360_3x4.jpg"));
        list.add(new Product("UQ070", "Stretch Easy Ankle Pants", "Pants", "MEN", 1990.0, 35,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/479134/item/phgoods_31_479134_3x4.jpg"));
        list.add(new Product("UQ071", "AIRism Boxer Briefs", "Innerwear", "MEN", 790.0, 24,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/454326/item/goods_38_454326_3x4.jpg"));
        list.add(new Product("UQ072", "BABY Toddler AIRism Cotton Crew Neck T-Shirt", "T-Shirts", "KIDS", 590.0, 18,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/481797/item/goods_12_481797_3x4.jpg"));
        list.add(new Product("UQ073", "Faux Fur Hooded Jacket", "Outerwear", "WOMEN", 2490.0, 30,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488723/item/phgoods_30_488723_3x4.jpg"));
        list.add(new Product("UQ074", "Embroidery Blouse Sleeveless", "Shirts & Blouses", "WOMEN", 1290.0, 12,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/487986/item/phgoods_41_487986_3x4.jpg"));
        list.add(new Product("UQ075", "Light Souffle Yarn Sweater", "Knitwear", "WOMEN", 2490.0, 40,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488840/item/phgoods_10_488840_3x4.jpg"));
        list.add(new Product("UQ076", "Baggy Barrel Leg Jeans", "Bags", "IT", 490.0, 8,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/480740/item/phgoods_31_480740_3x4.jpg"));
        list.add(new Product("UQ077", "Linen Blend Mini Dress Sleeveless", "Dresses", "WOMEN", 2490.0, 15,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/493376/item/goods_54_493376_3x4.jpg"));
        list.add(new Product("UQ078", "Pocketable UV Protection Parka", "Outerwear", "MEN", 2990.0, 22,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/469292/item/phgoods_17_469292_3x4.jpg"));
        list.add(new Product("UQ079", "Dry Color Crew Neck Short Sleeve T-Shirt", "T-Shirts", "MEN", 590.0, 5,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/465187/item/phgoods_26_465187_3x4.jpg"));
        list.add(new Product("UQ080", "Dry Pique Striped Polo Shirt", "Polos", "MEN", 1290.0, 35,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/485567/item/phgoods_26_485567_3x4.jpg"));
        list.add(new Product("UQ081", "3D Knit Turtle Neck Sweater", "Knitwear", "MEN", 1490.0, 24,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/491365/item/phgoods_64_491365_3x4.jpg"));
        list.add(new Product("UQ082", "Jersey Easy Work Pants", "Pants", "MEN", 1290.0, 18,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488739/item/phgoods_32_488739_3x4.jpg"));
        list.add(new Product("UQ083", "Reusable Bag (Cotton)", "Bags", "IT", 290.0, 30,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/462191/item/phgoods_01_462191_3x4.jpg"));
        list.add(new Product("UQ084", "Dry Sweat Full-Zip Hoodie", "Outerwear", "MEN", 1990.0, 12,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/483443/item/phgoods_60_483443_3x4.jpg"));
        list.add(new Product("UQ085", "GIRLS Denim Mini Skort", "Shorts & Skorts", "KIDS", 790.0, 40,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488738/item/phgoods_63_488738_3x4.jpg"));
        list.add(new Product("UQ086", "PUFFTECH Jacket", "Outerwear", "MEN", 3990.0, 8,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/487408/item/phgoods_19_487408_3x4.jpg"));
        list.add(new Product("UQ087", "Rayon Blouse", "Shirts & Blouses", "WOMEN", 1490.0, 15,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/489227/item/phgoods_19_489227_3x4.jpg"));
        list.add(new Product("UQ088", "Light Souffle Yarn Cardigan", "Knitwear", "WOMEN", 1990.0, 22,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488839/item/phgoods_19_488839_3x4.jpg"));
        list.add(new Product("UQ089", "Double Face Knit Dress", "Dresses", "WOMEN", 1490.0, 0,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488186/item/phgoods_09_488186_3x4.jpg"));
        list.add(new Product("UQ090", "Linen Blend Easy Pants | Longer Length", "Pants", "WOMEN", 1490.0, 35,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/473791/item/phgoods_30_473791_3x4.jpg"));
        list.add(new Product("UQ091", "AIRism Seamless Shorts | Just Waist", "Shorts & Skorts", "WOMEN", 790.0, 24,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/473945/item/goods_11_473945_3x4.jpg"));
        list.add(new Product("UQ092", "Coverall", "Outerwear", "MEN", 2990.0, 18,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/488828/item/phgoods_68_488828_3x4.jpg"));
        list.add(new Product("UQ093", "Dry Waffle Henley Neck T-Shirt", "T-Shirts", "MEN", 790.0, 30,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/483924/item/phgoods_30_483924_3x4.jpg"));
        list.add(new Product("UQ094", "Dry Pique Polo Shirt", "Polos", "MEN", 890.0, 12,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/485566/item/phgoods_12_485566_3x4.jpg"));
        list.add(new Product("UQ095", "3D Knit Mock Neck Sweater", "Knitwear", "MEN", 2490.0, 40,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/491363/item/phgoods_34_491363_3x4.jpg"));
        list.add(new Product("UQ096", "Jersey Easy Cargo Pants", "Pants", "MEN", 1990.0, 8,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/473177/item/phgoods_54_473177_3x4.jpg"));
        list.add(new Product("UQ097", "Gift Bag", "Bags", "IT", 190.0, 15,
                "https://image.uniqlo.com/UQ/ST3/AsianCommon/imagesgoods/484719/item/goods_01_484719_3x4.jpg"));
        list.add(new Product("UQ098", "GIRLS Pleated Mini Skort", "Shorts & Skorts", "KIDS", 590.0, 22,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/487273/item/phgoods_15_487273_3x4.jpg"));
        list.add(new Product("UQ099", "The Picture Book Collection Short Sleeve Bodysuit", "Innerwear", "KIDS", 690.0, 5,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/481745001/item/phgoods_60_481745001_3x4.jpg"));
        list.add(new Product("UQ100", "Reversible Down Stand Collar Jacket", "Outerwear", "MEN", 5990.0, 35,
                "https://image.uniqlo.com/UQ/ST3/ph/imagesgoods/487424/item/phgoods_18_487424_3x4.jpg"));
        return list;
    }
}