//
//  ViewController.swift
//  post_test
//
//  Created by YATAI on 2016/11/15.
//  Copyright © 2016年 YATAI. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    @IBOutlet weak var btn1: UIButton!

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        get_org_intro();
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    func get_org_intro(){
        DispatchQueue(label: "myQueueName1");
        let url:NSURL = NSURL(string: "http://220.135.161.128/afs_dale/ct/mbst_app/money_data/android_sql.php")!;
        let request:NSMutableURLRequest = NSMutableURLRequest(url: url as URL, cachePolicy: NSURLRequest.CachePolicy.reloadIgnoringLocalCacheData, timeoutInterval: 10);
        request.httpMethod = "POST";
        var  post_str:String="";
        post_str=post_str+"cmd=leo_test";
        request.httpBody = "cmd=leo_test&p1=aaa".data(using: String.Encoding.utf8);

        let task = URLSession.shared.dataTask(with: request as URLRequest) {
            data, response, error in
            
            if error != nil {
                print("error=\(error)")
                return
            }
            
            print("response = \(response)")
            
            //將收到的資料轉成字串print出來看看
            let responseString = NSString(data: data!, encoding: String.Encoding.utf8.rawValue)
            print("responseString = \(responseString)")
            DispatchQueue.main.async(execute: {
                let json = JSON(data: data!);
                var i:Int=0;
                var app_version:String = json[i]["app_version"].string!;
                self.btn1.setTitle(app_version, for:UIControlState.normal )
            })
                   }
        task.resume();
        
    }


}

