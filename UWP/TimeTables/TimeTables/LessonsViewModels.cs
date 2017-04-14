using SQLitePCL;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using Windows.Data.Xml.Dom;
using Windows.UI.Notifications;
using Windows.UI.Popups;

namespace TimeTables.ViewModels
{
    class LessonsViewModels
    {
        private ObservableCollection<Models.Lessons> allItems = new ObservableCollection<Models.Lessons>();
        public ObservableCollection<Models.Lessons> AllItems { get { return this.allItems; } }

        private ObservableCollection<Models.Lessons> sqlItems = new ObservableCollection<Models.Lessons>();
        public ObservableCollection<Models.Lessons> SQLItems { get { return this.sqlItems; } }

        private Models.Lessons selectedItem = default(Models.Lessons);
        public Models.Lessons SelectedItem { get { return selectedItem;} set { this.selectedItem = value; } }

        public string lastSelectWeek { get; set; }

        public LessonsViewModels()
        {
            //addLesson("数据库系统", "阮文江", "东C303", "星期五", 13, 15, true);
            //addLesson("管理沟通英语（大学英语）", "陈剑波", "东A501", "星期二", 4, 5, true);
            //addLesson("毛泽东思想和中国特色社会主义理论体系概念", "付春光", "B403", "星期一", 3, 6, true);
            //addLesson("计算机组成原理与接口技术实验", "小火龙", "实验中心C102", "星期三", 7, 9, true);
            //addLesson("操作系统", "张永东", "C403", "星期四", 3, 5, true);
            //addLesson("计组", "邓革", "C303", "星期五", 3, 5, true);

            //showClass(getWeekDay(System.DateTime.Today.DayOfWeek.ToString()));
            lastSelectWeek = System.DateTime.Today.DayOfWeek.ToString();
        }



        public void showClass(string week)
        {
            while (allItems.Count != 0)
                this.allItems.Remove(allItems[0]);
            using (var statement = App.conn.Prepare("SELECT Id, lessonName, lessonTeacher, classRoom, weekTime, startJie, endJie FROM lessons WHERE Id LIKE ?"))
            {
                statement.Bind(1, "%");
                while (SQLiteResult.ROW == statement.Step())
                {
                    if (statement[4].ToString() == week)
                        addLesson(statement[1].ToString(), statement[2].ToString(), statement[3].ToString(), statement[4].ToString(), int.Parse(statement[5].ToString()), int.Parse(statement[6].ToString()), long.Parse(statement[0].ToString()), false);
                    Models.Lessons Item = new Models.Lessons(statement[1].ToString(), statement[2].ToString(), statement[3].ToString(), statement[4].ToString(), int.Parse(statement[5].ToString()), int.Parse(statement[6].ToString()), long.Parse(statement[0].ToString()));
                    sqlItems.Add(Item);
                }
            }
            this.selectedItem = null;
        }

        //根据系统获得的时间显示是哪一天
        public string getWeekDay(string day)
        {
            string week = "";
            switch (day)
            {
                case "Monday":
                    week = "星期一";
                    break;
                case "Tuesday":
                    week = "星期二";
                    break;
                case "Wednesday":
                    week = "星期三";
                    break;
                case "Thursday":
                    week = "星期四";
                    break;
                case "Friday":
                    week = "星期五";
                    break;
                case "Saturday":
                    week = "星期六";
                    break;
                case "Sunday":
                    week = "星期日";
                    break;
            }
            return week;
        }

        //根据是哪一天显示是第几天 
        public int getWeekNum(string day)
        {
            int week = 0;
            switch (day)
            {
                case "星期一":
                    week = 1;
                    break;
                case "星期二":
                    week = 2;
                    break;
                case "星期三":
                    week = 3;
                    break;
                case "星期四":
                    week = 4;
                    break;
                case "星期五":
                    week = 5;
                    break;
                case "星期六":
                    week = 6;
                    break;
                case "星期日":
                    week = 7;
                    break;
            }
            return week;
        }

        public Models.Lessons LessonWillGo
        {
            get
            {
                if (allItems.Count == 0) return null;
                string weekDay = getWeekDay(System.DateTime.Today.DayOfWeek.ToString());
                for (int i = 0; i < allItems.Count; i++)
                {
                    if (allItems[i].weekTime == weekDay)
                    {
                        string timeNow = string.Format("{0:t}", System.DateTime.Now);
                        if (string.Compare(timeNow, allItems[i].getStartTime(allItems[i].startJie)) <= 0) return allItems[i];
                    }
                }
                return null;
            }
            set { }
        }

        public string printTodayLessons()
        {
            string text = "";
            for (int i = 0; i < allItems.Count; i++)
            {
                text += "课程名称：" + allItems[i].lessonName + "\n"
                      + "教室：　　" + allItems[i].classRoom + "\n"
                      + "老师：　　" + allItems[i].lessonTeacher + "\n"
                      + "节数：　　" + allItems[i].lessonJie + "\n\n";
            }
            return text;
        }

        /******************************************operation of database**************************************************/

        //1.insert to database
        public void SQLInsert(string name, string teacher, string room, string week, int start, int end)
        {
            var db = App.conn;
            try
            {
                using (var lesson = db.Prepare("INSERT INTO lessons(lessonName, lessonTeacher, classRoom, weekTime, startJie, endJie, rowid) VALUES (?,?,?,?,?,?,?)"))
                {
                    lesson.Bind(1, name);
                    lesson.Bind(2, teacher);
                    lesson.Bind(3, room);
                    lesson.Bind(4, week);
                    lesson.Bind(5, start);
                    lesson.Bind(6, end);
                    int t = getWeekNum(week) * 100 + start;
                    lesson.Bind(7, t);
                    lesson.Step();
                }
            }
            catch(Exception ex)
            {
                var x = new MessageDialog("Error: " + ex.ToString()).ShowAsync();
            }
            Models.Lessons Item = new Models.Lessons(name, teacher, room, week, start, end, App.conn.LastInsertRowId());
            sqlItems.Add(Item);
        }

        //2.delete from database
        public void SQLDelete(long id)
        {
            //remove from database
            try
            {
                using (var statement = App.conn.Prepare("DELETE FROM lessons WHERE Id = ?"))
                {
                    statement.Bind(1, id);
                    statement.Step();
                }
            }
            catch(Exception ex)
            {
                var x = new MessageDialog("Error: " + ex.ToString()).ShowAsync();
            }
            for (int i = 0; i < allItems.Count; i++)
                if (allItems[i].id == id)
                {
                    this.sqlItems.Remove(allItems[i]);
                    break;
                }
        }

        //3.update database
        public void SQLUpdate(string name, string teacher, string room, string week, int start, int end, long id)
        {
            try
            {
                using (var statement = App.conn.Prepare("UPDATE lessons SET lessonName=?, lessonTeacher=?, classRoom=?, weekTime=?, startJie=?, endJie=?, rowid=? WHERE id = ?"))
                {
                    statement.Bind(1, name);
                    statement.Bind(2, teacher);
                    statement.Bind(3, room);
                    statement.Bind(4, week);
                    statement.Bind(5, start);
                    statement.Bind(6, end);
                    int t = getWeekNum(week) * 100 + start;
                    statement.Bind(7,t);
                    statement.Bind(8, id);
                    statement.Step();
                }
            }
            catch (Exception ex)
            {
                var x = new MessageDialog("Error: " + ex.ToString()).ShowAsync();
            }
            for (int i = 0; i < sqlItems.Count; i++)
            {
                if (sqlItems[i].id == id)
                {
                    sqlItems[i].lessonName = name;
                    sqlItems[i].lessonTeacher = teacher;
                    sqlItems[i].classRoom = room;
                    sqlItems[i].startJie = start;
                    sqlItems[i].endJie = end;
                    sqlItems[i].lessonJie = sqlItems[i].getlessonJie(start, end);
                    sqlItems[i].lessonTime = sqlItems[i].getlessonTime(start, end);
                    sqlItems[i].weekTime = week;
                    sqlItems[i].id = getWeekNum(week) * 100 + start;
                    break;
                }
            }
        }

        //3.clear database
        public void SQLClear()
        {
            //remove from database
            try
            {
                using (var statement = App.conn.Prepare("DELETE FROM lessons WHERE Id LIKE ?"))
                {
                    statement.Bind(1, "%");
                    statement.Step();
                }
            }
            catch (Exception ex)
            {
                var x = new MessageDialog("Error: " + ex.ToString()).ShowAsync();
            }
            this.sqlItems.Clear();
        }

        /***********************************operation of viewmodel********************************************************/
        //add
        public void addLesson(string name, string teacher, string room, string week, int start, int end, long id, bool flag) {
            if (flag)
                SQLInsert(name, teacher, room, week, start, end);
        	Models.Lessons Item = new Models.Lessons(name, teacher, room, week, start, end, id);
            this.allItems.Add(Item);
            this.selectedItem = null;
        }
        public void addLesson(string name, string teacher, string room, string week, int start, int end, bool flag)
        {
            if (flag)
                SQLInsert(name, teacher, room, week, start, end);
            Models.Lessons Item = new Models.Lessons(name, teacher, room, week, start, end, App.conn.LastInsertRowId());
            this.allItems.Add(Item);
            this.selectedItem = null;
        }
        //remove
        public void removeLesson(long id, bool flag)
        {

            //remove from viewmodel
            for (int i = 0; i < allItems.Count; i++)
                if (allItems[i].id == id)
                {
                    this.allItems.Remove(allItems[i]);
                    if (flag)
                        SQLDelete(id);
                    break;
                }
            this.selectedItem = null;
        }
        public void removeLesson(bool flag)
        {
            if (selectedItem != null)
            {
                this.allItems.Remove(selectedItem);
                if (flag)
                    SQLDelete(selectedItem.id);
            }
            this.selectedItem = null;
        }
        public void removeLesson(Models.Lessons Item, bool flag)
        {
            this.allItems.Remove(Item);
            if (flag)
                SQLDelete(this.selectedItem.id);
            this.selectedItem = null;
        }



        //update
        public void updateLesson(int id, string name, string teacher, string room, string week, int start, int end)
        {
            for (int i = 0; i < allItems.Count; i++)
            {
                if (allItems[i].id == id)
                {
                    allItems[i].lessonName = name;
                    allItems[i].lessonTeacher = teacher;
                    allItems[i].classRoom = room;
                    allItems[i].startJie = start;
                    allItems[i].endJie = end;
                    allItems[i].lessonJie = allItems[i].getlessonJie(start, end);
                    allItems[i].lessonTime = allItems[i].getlessonTime(start, end);
                    allItems[i].weekTime = week;
                    SQLUpdate(name, teacher, room, week, start, end, id);
                    allItems[i].id = getWeekNum(week) * 100 + start;
                    break;
                }
            }
            this.selectedItem = null;
        }

    }
}
