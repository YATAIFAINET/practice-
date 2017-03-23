//
//  ViewController.swift
//  Fainet_Memory
//
//  Created by YTT1 on 2016/11/4.
//  Copyright © 2016年 YTT1. All rights reserved.
//

import UIKit
import Foundation

class tmp :Cancelable {
 
   //calss init and extend
    //var zxzccxz: String = "dsaasd";
    var tttasdadsdas :String!;
    init() {
        _init(_tt: 123, _t2: "adsdsadas")
    }

    func _init (_tt:Int , _t2:String) -> Void {
        print(zxzccxz);
    }
    func pr() -> Void {
        print("tmp");
    }
    
    func pri() -> Void {
        print("tmppign");
    }
}

class ViewController: UIViewController  {

    
    enum GC :Int {
        //This cmd on GC with Switch
        case Login ;
        case Fix ;
        case Delete ;
        case Select ;
    }
    
    enum GCString : String {
        case QQ = "aaaQQ";
        case WW = "aaWW";
        case DD = "aDDD";
    }
   
    struct rgb {
        var Red :Int8?;
        var Green :Int8?;
        var Blue : Int8?;
        
    }
    
    override func viewDidLoad() {
    
        let dsasd = tmp.init();
        dsasd.cancel();

        var ttrgb = rgb.init();
        ttrgb.Blue = 100;
        super.viewDidLoad()
        let QrInt :Int = 0;
        var pl :String = "tmp"
        print(QrInt);
        
        switch QrInt {
        case GC.Login.rawValue:
            print("00000000");
            break;
        case GC.Fix.rawValue:
            print("11111111");
            break;
        case GC.Delete.rawValue:
            print("22222222");
            break;
        case GC.Select.rawValue:
            print("33333333");
            break;
        default:
            print("44444444");
            break;
        }
        
        // Do any additional setup after loading the view, typically from a nib.
        rrrrr();
        arraytmp();
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    func StringToInt(Stringtmp :String) -> Int {
        
        return 0;
    }
    //
    //  GCMD.h
    //  Fainet_Memory
    //
    //  Created by YTT1 on 2016/11/8.
    //  Copyright © 2016年 YTT1. All rights reserved.
    //
    
    /*
     #include <sqlite3.h>     //lib
     #import "YTPlayerView.h" //.h
     */
    
}
/*
 
 override func viewWillAppear(_ animated: Bool) {
 super.viewWillAppear(animated)
 self.InitSetting();
 print("viewWillAppear")
 }
 
 /*
 viewWillAppear()：在viewDidLoad()之後被執行，時間點在 View 要被呈現前，每次切換到這個頁面時都會執行。
 viewDidAppear()：在viewWillAppear()之後被執行，時間點在 View 呈現後，每次切換到這個頁面時都會執行。
 viewWillDisappear()：執行的時間點在 View 要結束前，每次要切換到別頁或是退出這個頁面時都會執行。
 viewDidDisappear()：執行的時間點在 View 完全結束後，每次要切換到別頁或是退出這個頁面時都會執行。
 */
 override func viewDidAppear(_ animated: Bool) {
 super.viewDidAppear(animated)
 
 print("viewDidAppear")
 }
 
 override func viewWillDisappear(_ animated: Bool) {
 super.viewWillDisappear(animated)
 print("viewWillDisappear")
 }
 
 override func viewDidDisappear(_ animated: Bool) {
 super.viewDidDisappear(animated)
 
 print("viewDidDisappear")
 }
 
 override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
 
 if ((touches.first != nil && touches.first?.view == self.Forget_Password_View)) {
 self.Forget_Password_View.isHidden = true;
 self.mAccount.resignFirstResponder();
 self.mPassword.resignFirstResponder();
 print("Forget_Password_View is true");
 }
 self.mAccount.resignFirstResponder();
 self.mPassword.resignFirstResponder();
 
 }
 */


//
/*
 class Money_Bag_Mana:UIViewController {
 
 //define param
 private let mGCMD_LIB :GCMD_LIB = GCMD_LIB.init();
 //--------------------------------------------------------------------------------------------------
 //layout
 
 //--------------------------------------------------------------------------------------------------
 //dialog
 //---------------------------------------------------------------------------------------------------
 override func viewDidLoad() {
 super.viewDidLoad()
 self.InitSetting();
 // Do any additional setup after loading the view, typically from a nib.
 }
 
 //--------------------------------------------------------------------------------------------------
 override func didReceiveMemoryWarning() {
 super.didReceiveMemoryWarning()
 // Dispose of any resources that can be recreated.
 }
 
 //--------------------------------------------------------------------------------------------------
 override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
 return UIInterfaceOrientationMask.portrait;
 }
 
 override var shouldAutorotate: Bool {
 return false;
 }
 
 //--------------------------------------------------------------------------------------------------
 //func Lib
 func InitSetting() -> Void {
 print("InitSetting");
 //alert init
 //----------
 }
 //--------------------------------------------------------------------------------------------------
 //Action
 
 @IBAction func Money_Bag_Mana_Return (_ sender :UIButton)->Void {
 print("Favorite_Return");
 mGCMD_LIB.Icon_Class.Pop_DissSelfMemory(Source_View: Changeoption.self, Self_Re: self);
 
 }
 //--------------------------------------------------------------------------------------------------
 //toolbar icon action
 @IBAction func Home_Page_Icon (_ sender :UIButton)->Void {
 print("Home_Page_Icon")
 }
 @IBAction func Money_Bag_Icon (_ sender :UIButton)->Void {
 print("Money_Bag_Icon")
 }
 @IBAction func Favorite_Icon (_ sender :UIButton)->Void {
 print("Favorite_Icon")
 }
 @IBAction func Ticket_Icon (_ sender :UIButton)->Void {
 print("Ticket_Icon")
 }
 @IBAction func My_Data_Icon (_ sender :UIButton)->Void {
 print("My_Data_Icon")
 }
 
 //--------------------------------------------------------------------------------------------------
 //Thread
 //--------------------------------------------------------------------------------------------------
 }
 */

//====

/*
 func StringToInt (tmpStr:String) ->Int {
 return Int(tmpStr)!;
 }
 
 func StringToFloat(tmpStr:String) -> Float {
 return Float(tmpStr)!;
 }
 
 func StringToDouble(tmpStr:String) -> Double {
 return Double(tmpStr)!;
 }
 
 func IntToString(tmpInt:Int) -> String {
 return String(tmpInt);
 }
 
 func FloatToString(tmpFloat:Float) -> String {
 return  String(tmpFloat);
 }
 
 func StringToByte(tmpStr:String) -> [UInt8] {
 let byteArray: [UInt8] = Array(tmpStr.utf8);
 return byteArray;
 }
 
 func StringToCharArray( tmpStr: String ) -> [Character] {
 let mChartmp :[Character] = Array (tmpStr.characters);
 return mChartmp;
 }
 */

//===

//--------------------------------------------------------------------------------
public func StringSubString (SourceRes :String , SourceLen:Int, TargetLen:Int) ->String {
    
    let Target :Int = (0-SourceRes.characters.count)+TargetLen;
    let Source :Int = SourceLen;
    
    let start = SourceRes.index(SourceRes.startIndex, offsetBy: Source)
    let end = SourceRes.index(SourceRes.endIndex, offsetBy: Target)
    let range = start..<end
    
    let TargetRes :String = SourceRes.substring(with: range);
    return TargetRes;
}
//-------------------------------------------------------------------------------
public func StringSplitToArray (SourceRes :String,Splitmark :String) ->[String] {
    let Target = SourceRes.components(separatedBy: Splitmark);
    return Target;
}
//-------------------------------------------------------------------------------

