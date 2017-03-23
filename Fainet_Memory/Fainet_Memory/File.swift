//
//  File.swift
//  Fainet_Memory
//
//  Created by YTT1 on 2016/11/10.
//  Copyright © 2016年 YTT1. All rights reserved.
//

import Foundation

protocol Cancelable {
    
    //var zxzccxz :String   {get set}
    func cancel();
}

extension Cancelable {
    
    var  zxzccxz :String {return "Check Out"};
    
    func cancel() {
        print("cancel");
    }
}

struct argb {                       //THis is include Image Porcessign Struct
    var Alphe :CUnsignedChar!;
    var Red :CUnsignedChar!;
    var Green :CUnsignedChar!;
    var Blue :CUnsignedChar!;
}

func rrrrr() -> Void {
    print("saddssadsadsadsa");
}
func arraytmp() ->Void {
    

    var asdsda :[Int] = [1,2,3]
    
    var ttt :[Int] = [4,5,6];
    var qwe :[Int] = [];
    
    qwe = Array (repeating:Int(), count:asdsda.count);
    
    var arrrect :[[Int]] = [[]];
    
    arrrect = Array(repeating:Array(repeating:Int(),count:10), count :5); //[5][10]
   

    print(arrrect[0].count);
    /*
    var QWC :[Int] = [];  // dynamic
    
    var QAZ :[Int] = Array(1...3); //Array 1-3
    
    var QZA :[Character] = Array ("hey".characters); //QZA Char Byte
    
    //var arr :[[Int]] = [[0,1,2],[2,3,4,5]]; //array to 2 rect
    var arr : [[Int]] = [[]];
    
    QWC = Array(repeating:Int(), count:5)
    
    arr = Array(repeating:Array(repeating:Int(), count:5), count:5) //two rect on array and dynamic

   // for i in 0...(QWC.count-1){
   //     print(QWC[i]);
   // }
    print(QWC.count);
    
    print(QWC.count);
    
    print(QAZ.count);
    
    print(QZA.count);
    
    for i in 0...(QZA.count-1) {
            print(QZA[i]);
    }
    
    for i in 0...QWC.count-1 {
        
        if(QWC[i] != nil){
            print(QWC[i]);
        } else {
            print("QWC is null");
        }
        
    }

    print(arr[0].count);
    
    print(arr.count);
    
    print(arr[0][0]);
    print(arr[0][1]);
    print(arr[0][2]);
    print(arr[1][0]);
    print(arr[1][1]);
    print(arr[1][2]);
    print(arr[1][3]);
 */
}
