//
//  ViewController.swift
//  Sheng_Object
//
//  Created by YTT1 on 2016/11/4.
//  Copyright © 2016年 YTT1. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var Text: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        //動態產生Button
        let button = UIButton.init(type: UIButtonType.system)
        button.frame = CGRect(x: 50, y: 50, width: 100, height: 30)
        button.backgroundColor = UIColor.green
        button.setTitle("Hellow World", for: UIControlState.normal)
        button.addTarget(self, action: Selector(("buttonPress:")), for: UIControlEvents.touchUpInside)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

