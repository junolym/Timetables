using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

// “空白页”项模板在 http://go.microsoft.com/fwlink/?LinkId=234238 上提供

namespace TimeTables
{
    /// <summary>
    /// 可用于自身或导航至 Frame 内部的空白页。
    /// </summary>
    public sealed partial class LessonEditPage : Page
    {
        public LessonEditPage()
        {
            this.InitializeComponent();
        }

        private ViewModels.LessonsViewModels ViewModel;

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            ViewModel = ((ViewModels.LessonsViewModels)e.Parameter);
            if (ViewModel.SelectedItem != null)
            {
                lessonname.Text = ViewModel.SelectedItem.lessonName;
                lessonteacher.Text = ViewModel.SelectedItem.lessonTeacher;
                lessonroom.Text = ViewModel.SelectedItem.classRoom;
                starttime.PlaceholderText = ViewModel.SelectedItem.startJie.ToString();
                endtime.PlaceholderText = ViewModel.SelectedItem.endJie.ToString();
                day.PlaceholderText = ViewModel.SelectedItem.weekTime;
                addlessonbotton.Visibility = Visibility.Collapsed;
                editlessonbotton.Visibility = Visibility.Visible;
                DeletelessonButton.Visibility = Visibility.Visible;
            }
            else
            {
                lessonname.Text = "";
                lessonteacher.Text = "";
                lessonroom.Text = "";
                starttime.PlaceholderText = "1";
                endtime.PlaceholderText = "1";
                day.PlaceholderText = ViewModel.getWeekDay(ViewModel.lastSelectWeek);
                addlessonbotton.Visibility = Visibility.Visible;
                editlessonbotton.Visibility = Visibility.Collapsed;
            }
        }

        private void starttime_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            //endtime.PlaceholderText = starttime.SelectionBoxItem.ToString();
            if (endtime.SelectedIndex < starttime.SelectedIndex)
                endtime.SelectedIndex = starttime.SelectedIndex;
        }

        private void endtime_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            //endtime.PlaceholderText = starttime.SelectionBoxItem.ToString();
            if (starttime.SelectedIndex > endtime.SelectedIndex)
                starttime.SelectedIndex = endtime.SelectedIndex;
        }

        private void editlessonbotton_Click(object sender, RoutedEventArgs e)
        {
            int startjie, endjie;
            if (starttime.SelectedIndex.ToString() == "-1")
                startjie = int.Parse(starttime.PlaceholderText);
            else
                startjie = int.Parse(starttime.SelectionBoxItem.ToString());
            if (endtime.SelectedIndex.ToString() == "-1")
                endjie = int.Parse(endtime.PlaceholderText);
            else
                endjie = int.Parse(endtime.SelectionBoxItem.ToString());
            string weekday;
            if (day.SelectedIndex.ToString() == "-1")
                weekday = day.PlaceholderText;
            else
                weekday = day.SelectionBoxItem.ToString();
            string wrong = "";
            if (check_noconflict(startjie, endjie, weekday, ViewModel.SelectedItem.id))
            {
                //var i = new MessageDialog(((int)ViewModel.SelectedItem.id).ToString()).ShowAsync();
                ViewModel.updateLesson((int)ViewModel.SelectedItem.id, lessonname.Text, lessonteacher.Text, lessonroom.Text, weekday, startjie, endjie);
                wrong = "修改新课程成功";
                Frame.Navigate(typeof(MainPage), ViewModel);
            }
            else
                wrong = "课程时间冲突，请重新输入";
            var i = new MessageDialog(wrong).ShowAsync();
        }

        private bool check_noconflict(int starttime, int endtime, string weekday, long id)
        {
            for (int i = 0; i < ViewModel.SQLItems.Count; i++)
            {
                if (ViewModel.SQLItems[i].weekTime == weekday && ViewModel.SQLItems[i].id != id)
                {
                    if (starttime < ViewModel.SQLItems[i].startJie && endtime >= ViewModel.SQLItems[i].startJie)
                        return false;
                    if (starttime >= ViewModel.SQLItems[i].startJie && starttime <= ViewModel.SQLItems[i].endJie)
                        return false;
                }
            }
            return true;
        }

        private void addlessonbotton_Click(object sender, RoutedEventArgs e)
        {
            string wronginformation = "";
            if (lessonname.Text == "" || lessonteacher.Text == "" || lessonroom.Text == "")
            {
                wronginformation = "内容不能为空";
            }
            else {
                int startjie, endjie;
                if (starttime.SelectedIndex.ToString() == "-1")
                    startjie = int.Parse(starttime.PlaceholderText);
                else
                    startjie = int.Parse(starttime.SelectionBoxItem.ToString());
                if (endtime.SelectedIndex.ToString() == "-1")
                    endjie = int.Parse(endtime.PlaceholderText);
                else
                    endjie = int.Parse(endtime.SelectionBoxItem.ToString());
                string weekday;
                if (day.SelectedIndex.ToString() == "-1")
                    weekday = day.PlaceholderText;
                else
                    weekday = day.SelectionBoxItem.ToString();
                if (check_noconflict(startjie, endjie, weekday, 0))
                {
                    //var i = new MessageDialog(((int)ViewModel.SelectedItem.id).ToString()).ShowAsync();
                    ViewModel.addLesson(lessonname.Text, lessonteacher.Text, lessonroom.Text, weekday, startjie, endjie, true);
                    wronginformation = "创建新课程成功";
                    Frame.Navigate(typeof(MainPage), ViewModel);
                }
                else
                    wronginformation = "课程时间冲突，请重新输入";
            }
            var i = new MessageDialog(wronginformation).ShowAsync();
        }

        private void CancleButton_Click(object sender, RoutedEventArgs e)
        {
            if (editlessonbotton.Visibility == Visibility.Visible)
                Frame.Navigate(typeof(MainPage), ViewModel);
            else
            {
                lessonname.Text = "";
                lessonteacher.Text = "";
                lessonroom.Text = "";
                starttime.PlaceholderText = "1";
                endtime.PlaceholderText = "1";
                day.PlaceholderText = ViewModel.getWeekDay(ViewModel.lastSelectWeek);
            }
        }

        private void DeletelessonButton_Click(object sender, RoutedEventArgs e)
        {
            ViewModel.removeLesson(ViewModel.SelectedItem, true);
            Frame.Navigate(typeof(MainPage), ViewModel);
        }
    }
}
