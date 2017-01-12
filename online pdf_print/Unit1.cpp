//---------------------------------------------------------------------------

#include <vcl.h>
#pragma hdrstop
#include <windows.h>
#include <wininet.h>
#include "Unit1.h"
#include <HTTPApp.hpp>
#pragma comment(lib,"wininet.lib")
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma link "SHDocVw_OCX"
#pragma resource "*.dfm"
#include <Urlmon.h>
TForm1 *Form1;
//---------------------------------------------------------------------------
__fastcall TForm1::TForm1(TComponent* Owner)
        : TForm(Owner)
{
}
//---------------------------------------------------------------------------

void TForm1::to_download(const char *Url,const char *save_as)
{
   byte Temp[1024];
   ULONG Number = 1;

   FILE *stream;
   HINTERNET hSession = InternetOpen("RookIE/1.0", INTERNET_OPEN_TYPE_PRECONFIG, NULL, NULL, 0);//(LPCWSTR)
 if (hSession != NULL)
 {
  HINTERNET handle2 = InternetOpenUrl(hSession, Url, NULL, 0, INTERNET_FLAG_DONT_CACHE, 0);  //(LPCWSTR)
  if (handle2 != NULL)
  {


   if( (stream = fopen( save_as, "wb" )) != NULL )
   {
    while (Number > 0)
    {
     InternetReadFile(handle2, Temp, 1024 - 1, &Number);

     fwrite(Temp, sizeof (char), Number , stream);


    }
    fclose( stream );

    ShellExecute(NULL,NULL,"C:\\abc.bat","", NULL,SW_SHOW );
   }

   InternetCloseHandle(handle2);
   handle2 = NULL;
  }
  InternetCloseHandle(hSession);
  hSession = NULL;
 }
}






void __fastcall TForm1::ServerSocket1ClientRead(TObject *Sender,
      TCustomWinSocket *Socket)
{

AnsiString str;
str= Socket->ReceiveText();
ShowMessage(str);
//to_download(str.c_str(),"C:\\print.pdf");

ServerSocket1->Socket->Connections[0]->SendText("ok");
}
//---------------------------------------------------------------------------



