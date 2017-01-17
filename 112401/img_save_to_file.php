<?php
/*
*	!!! THIS IS JUST AN EXAMPLE !!!, PLEASE USE ImageMagick or some other quality image processing libraries
*/
    $imagePath = "temp/";

	$allowedExts = array("gif", "jpeg", "jpg", "png", "GIF", "JPEG", "JPG", "PNG");
	$temp = explode(".", $_FILES["img"]["name"]);
	$extension = end($temp);
	
	//Check write Access to Directory

	if(!is_writable($imagePath)){
		$response = Array(
			"status" => 'error',
			"message" => 'Can`t upload File; no write Access'
		);
		print json_encode($response);
		return;
	}
	
	if ( in_array($extension, $allowedExts))
	  {
	  if ($_FILES["img"]["error"] > 0)
		{
			 $response = array(
				"status" => 'error',
				"message" => 'ERROR Return Code: '. $_FILES["img"]["error"],
			);			
		}
	  else
		{
			
	      $filename = $_FILES["img"]["tmp_name"];
		  list($width, $height) = getimagesize( $filename );

		  

		  
		  move_uploaded_file($filename,$imagePath.$_FILES["img"]["name"]);

		  /*====================*/
		  //倍率
		  $big_c=4000;
		  $chk=0;

		  if($width>$height){
		  	 while($width>=$big_c){
		  		$width=$width/2;
		  		$height=$height/2;
		  		$chk++;
		 	 }
		  }else{
		  	while($height>=$big_c){
		  		$width=$width/2;
		  		$height=$height/2;
		  		$chk++;
		 	 }
		  }


		  $what = getimagesize($imagePath.$_FILES["img"]["name"]);
		  //echo $what['mime'];
			
			switch(strtolower($what['mime']))
			{
			    case 'image/png':
			        $src = imagecreatefrompng($imagePath.$_FILES["img"]["name"]);
					$source_image = imagecreatefrompng($imagePath.$_FILES["img"]["name"]);
					$type = '.png';
			        break;
			    case 'image/jpeg':
			        $src = imagecreatefromjpeg($imagePath.$_FILES["img"]["name"]);
					$source_image = imagecreatefromjpeg($imagePath.$_FILES["img"]["name"]);
					//error_log("jpg");
					$type = '.jpeg';
			        break;
			    case 'image/gif':
			        $src = imagecreatefromgif($imagePath.$_FILES["img"]["name"]);
					$source_image = imagecreatefromgif($imagePath.$_FILES["img"]["name"]);
					$type = '.gif';
			        break;
			    default: die('image type not supported');
			}

		  if($chk>0){


		    $thumb_w = $width; 
			$thumb_h = $height; 
			//echo $imagePath.$_FILES["img"]["name"];
			//$what = getimagesize($imagePath.$_FILES["img"]["name"]);
			//echo $what['mime'];
			
			

			//$src = imagecreatefromjpeg($imagePath.$_FILES["img"]["name"]);


			$thumb = imagecreatetruecolor($thumb_w, $thumb_h); 
			//$thumb=imagecopyresampled($thumb, $src, 0, 0, 0, 0, $thumb_w, $thumb_h, $width, $height); 


			// 開始縮圖
			imagecopyresampled($thumb, $src, 0, 0, 0, 0, $thumb_w, $thumb_h, $width, $height);

			// 儲存縮圖到指定 thumb 目錄
			imagejpeg($thumb, $imagePath.$_FILES["img"]["name"]);

			//$img_name=$imagePath.date("Y-m-d H:i:s");
			//echo $imagePath.$_FILES["img"]["name"];
			$img_name=$imagePath.strtotime(date("Y-m-d H:i:s")).$type;
			// 複製上傳圖片到指定 images 目錄
			copy($imagePath.$_FILES["img"]["name"],$img_name);

			
		  }else{

		  	$img_name=$imagePath.strtotime(date("Y-m-d H:i:s")).$type;
		  	copy($imagePath.$_FILES["img"]["name"],$img_name);
		  	$img_name=$imagePath.strtotime(date("Y-m-d H:i:s")).$type;
		  }
		  unlink($imagePath.$_FILES["img"]["name"]);

		  /*====================*/

		  $response = array(
			"status" => 'success',
			"url" => $img_name,
			"width" => $width,
			"height" => $height
		  );
		  
		}
	  }
	else
	  {
	   $response = array(
			"status" => 'error',
			"message" => 'something went wrong, most likely file is to large for upload. check upload_max_filesize, post_max_size and memory_limit in you php.ini',
		);
	  }
	  
	  print json_encode($response);

?>
