using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TimeTables.Models
{
    class Lessons : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;
        public string lessonName{get; set;}
        public string lessonTeacher{get; set;}
        public string classRoom{get; set;}
        public string weekTime{get; set;}
        public int startJie { get; set; }
        public int endJie { get; set; }
        public string lessonJie{get; set;}
        public string lessonTime{get; set;}
        public long id{get; set;}

        //获取上课和下课时间
        public string getStartTime(int time)
        {
            string[] classes = {"08:00", "08:55", "09:50", "10:45", "11:40", "12:35",
            "13:30", "14:25", "15:20", "16:15", "17:10", "18:05", "19:00", "19:55", "20:50"};
            return classes[time - 1];
        }
        public string getEndTime(int time)
        {
            string[] classes = {"08:45", "09:40", "10:35", "11:30", "12:25", "13:20",
            "14:15", "15:10", "16:05", "17:00", "17:55", "18:50", "19:45", "20:40", "21:35"};
            return classes[time - 1];
        }

        //获得上下课的信息
        public string getlessonJie(int start, int end)
        {
            return "第" + start.ToString() + " ~ " + end.ToString() + " 节";
        }
        public string getlessonTime(int start, int end)
        {
            return getStartTime(start) + " ~ " + getEndTime(end); ;
        }

        public Lessons(string name, string teacher, string room, string week, int start, int end, long sid) {
        	lessonName = name;
        	lessonTeacher = teacher;
        	classRoom = room;
        	weekTime = week;
            startJie = start;
            endJie = end;
            lessonJie = getlessonJie(start, end);
            lessonTime = getlessonTime(start, end);
        	id = sid;
        }
    }
}
