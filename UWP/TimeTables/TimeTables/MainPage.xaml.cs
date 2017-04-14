using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.ApplicationModel.DataTransfer;
using Windows.Data.Xml.Dom;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI;
using Windows.UI.Notifications;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

//“空白页”项模板在 http://go.microsoft.com/fwlink/?LinkId=402352&clcid=0x409 上有介绍

namespace TimeTables
{
    /// <summary>
    /// 可用于自身或导航至 Frame 内部的空白页。
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public MainPage()
        {
            this.InitializeComponent();
            this.ViewModel = new ViewModels.LessonsViewModels();
            var viewTitleBar = Windows.UI.ViewManagement.ApplicationView.GetForCurrentView().TitleBar;
            viewTitleBar.BackgroundColor = Windows.UI.Colors.CornflowerBlue;
            viewTitleBar.ButtonBackgroundColor = Windows.UI.Colors.CornflowerBlue;
        }
        ViewModels.LessonsViewModels ViewModel { get; set; }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);
            if (e.Parameter.GetType() == typeof(ViewModels.LessonsViewModels))
            {
                this.ViewModel = (ViewModels.LessonsViewModels)(e.Parameter);
            }
            Windows.UI.Core.SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = Windows.UI.Core.AppViewBackButtonVisibility.Collapsed;
            updateTiles();
            //初始化选择显示当前天，将其对应的button变色
            ((Button)this.FindName(ViewModel.lastSelectWeek)).Background
                = new SolidColorBrush(Colors.Gray);
            ViewModel.showClass(ViewModel.getWeekDay(ViewModel.lastSelectWeek));
            ViewModel.SelectedItem = null;      
        }

        //button选中时颜色的变化
        private void colorChange(string week)
        {
            ((Button)this.FindName(ViewModel.lastSelectWeek)).Background
                = new SolidColorBrush(Colors.LightGray);
            ((Button)this.FindName(week)).Background
                = new SolidColorBrush(Colors.Gray);
            ViewModel.lastSelectWeek = week;
        }

        //选择不同天对应的课程显示以及button的变化
        private void MondayClick(object sender, RoutedEventArgs e)
        {
            colorChange("Monday");
            ViewModel.showClass("星期一");
        }
        private void TuesdayClick(object sender, RoutedEventArgs e)
        {
            colorChange("Tuesday");
            ViewModel.showClass("星期二");
        }
        private void WednesdayClick(object sender, RoutedEventArgs e)
        {
            colorChange("Wednesday");
            ViewModel.showClass("星期三");
        }
        private void ThursdayClick(object sender, RoutedEventArgs e)
        {
            colorChange("Thursday");
            ViewModel.showClass("星期四");
        }
        private void FridayClick(object sender, RoutedEventArgs e)
        {
            colorChange("Friday");
            ViewModel.showClass("星期五");
        }
        private void SaturdayClick(object sender, RoutedEventArgs e)
        {
            colorChange("Saturday");
            ViewModel.showClass("星期六");
        }
        private void SundayClick(object sender, RoutedEventArgs e)
        {
            colorChange("Sunday");
            ViewModel.showClass("星期日");
        }

        //显示选中课程的详细信息
        private void Lesson_ItemClicked(object sender, ItemClickEventArgs e)
        {
            ViewModel.SelectedItem = (Models.Lessons)(e.ClickedItem);
            Frame.Navigate(typeof(LessonEditPage), ViewModel);
        }

        private void ShareBarButton_Click(object sender, RoutedEventArgs e)
        {
            MenuFlyoutItem share = sender as MenuFlyoutItem;
            //ViewModel.SelectedItem = ViewModel.findOne(share.Tag.ToString());
            DataTransferManager.GetForCurrentView().DataRequested += OnShareDateRequested;
            DataTransferManager.ShowShareUI();
        }

        public void OnShareDateRequested(DataTransferManager sender, DataRequestedEventArgs args)
        {
            var request = args.Request;

            /*设置分享数据*/
            request.Data.Properties.Title = ViewModel.getWeekDay(ViewModel.lastSelectWeek) + "的课程";
            request.Data.Properties.Description = "在" + ViewModel.getWeekDay(ViewModel.lastSelectWeek) + "要上的课程的详细信息";
            request.Data.SetText(ViewModel.printTodayLessons());
        }

        //更新磁贴
        private void updateTiles()
        {
            var updater = TileUpdateManager.CreateTileUpdaterForApplication();
            updater.Clear();

            string xml;
            XmlDocument doc = new XmlDocument();
            /*如果没有课程就什么都不显示*/
            if (ViewModel.LessonWillGo == null)
            {
                string s0 = "今天没课！\n";
                string s1 = "找个女朋友吧~\n";
                string s2 = "嘿嘿嘿嘿嘿嘿";
                if (ViewModel.AllItems.Count != 0)
                {
                    s0 = "今天的课上完了哦~\n";
                }
                xml = string.Format(File.ReadAllText("tiles.xml") + "\n",
                    s0,
                    "",
                    s1,
                    "",
                    s2
                    );
            }
            else
            {
                xml = string.Format(File.ReadAllText("tiles.xml") + "\n",
                    ViewModel.LessonWillGo.lessonName.ToString(),
                    ViewModel.LessonWillGo.classRoom.ToString(),
                    ViewModel.LessonWillGo.lessonTeacher.ToString(),
                    ViewModel.LessonWillGo.lessonJie.ToString(),
                    ViewModel.LessonWillGo.lessonTime.ToString());
            }

            doc.LoadXml(WebUtility.HtmlDecode(xml), new XmlLoadSettings
            {
                ProhibitDtd = false,
                ValidateOnParse = false,
                ElementContentWhiteSpace = false,
                ResolveExternals = false
            });
            var notification = new TileNotification(doc);
            updater.Update(notification);
        }

        private void AddlessonButton_Click(object sender, RoutedEventArgs e)
        {
            ViewModel.SelectedItem = null;
            Frame.Navigate(typeof(LessonEditPage), ViewModel);
        }

        private void GetlessonButton_Click(object sender, RoutedEventArgs e)
        {
            Frame.Navigate(typeof(ImportPage), ViewModel);
        }
    }
}
