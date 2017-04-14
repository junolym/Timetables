using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Text;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;
using Windows.UI.Xaml.Navigation;

// “空白页”项模板在 http://go.microsoft.com/fwlink/?LinkId=234238 上提供

namespace TimeTables
{
    /// <summary>
    /// 可用于自身或导航至 Frame 内部的空白页。
    /// </summary>
    public sealed partial class ImportPage : Page
    {
        public ImportPage()
        {
            this.InitializeComponent();
        }
        private ViewModels.LessonsViewModels ViewModel;
        private string url = "http://api.chenjx.cn/kcb?v=";
        private string rnd = "";

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            ViewModel = ((ViewModels.LessonsViewModels)e.Parameter);
        }

        private async void getBtn_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                info.Text = "获取中，请稍候";
                HttpClient httpClient = new HttpClient();

                var values = new Dictionary<string, string>
                {
                    { "username", username.Text },
                    { "password", password.Password },
                    { "j_code", j_code.Text }
                };

                var content = new FormUrlEncodedContent(values);

                HttpResponseMessage response = await httpClient.PostAsync(url + rnd, content);

                response.EnsureSuccessStatusCode();

                Byte[] getByte = await response.Content.ReadAsByteArrayAsync();
                Encoding code = Encoding.GetEncoding("UTF-8");
                string result = code.GetString(getByte, 0, getByte.Length);
                info.Text = "获取成功，正在解析数据";
                //info.Text = result;
                JsonTextReader json = new JsonTextReader(new StringReader(result));
                while (json.Read())
                {
                    if ("" + json.TokenType == "StartArray")
                    {
                        ViewModel.SQLClear();
                        json.Read();
                        while ("" + json.TokenType == "StartObject")
                        {
                            json.Read();
                            string name = "", teacher = "", room = "", week = "";
                            int start = 0, end = 0;
                            while ("" + json.TokenType != "EndObject")
                            {
                                string type = "" + json.Value;
                                json.Read();
                                switch (type)
                                {
                                    case "name": name = "" + json.Value; break;
                                    case "week": week = "" + json.Value; break;
                                    case "room": room = "" + json.Value; break;
                                    case "teacher": teacher = "" + json.Value; break;
                                    case "start": int.TryParse("" + json.Value, out start); break;
                                    case "end": int.TryParse("" + json.Value, out end); break;
                                }
                                json.Read();
                            }
                            json.Read();
                            ViewModel.SQLInsert(name, teacher, room, week, start, end);
                        }
                    }
                    else
                    {
                        json.Read();
                        if ("" + json.Value == "error")
                        {
                            json.Read();
                            info.Text = "" + json.Value;
                            refreshImage();
                        }
                        else
                        {
                            info.Text = "未知错误";
                        }
                    }
                }
                info.Text = "成功录入";
            }
            catch (Exception)
            {
                info.Text = "网络状况不佳，请稍候再试";
            }

        }

        private void refreshImage(object sender, RoutedEventArgs e)
        {
            refreshImage();
        }

        private void refreshImage(object sender, TappedRoutedEventArgs e)
        {
            refreshImage();
        }

        private void refreshImage()
        {
            rnd = new Random().Next().ToString();
            Uri uri = new Uri(url + rnd, UriKind.RelativeOrAbsolute);
            BitmapImage bitmapImage = new BitmapImage(uri);
            image.Source = bitmapImage;
        }
    }
}
