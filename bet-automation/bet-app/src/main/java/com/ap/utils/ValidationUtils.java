package com.ap.utils;


import com.ap.model.MomentResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ValidationUtils {

    public static void main(String[] args) throws IOException {
        String results = "[{\"coef1\":8.22,\"coef2\":4.0,\"result\":\"\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:35:40.298\"},{\"coef1\":2.9,\"coef2\":1.38,\"result\":\"\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:36:13.698\"},{\"coef1\":2.75,\"coef2\":1.42,\"result\":\"0-0(1-0)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:36:24.798\"},{\"coef1\":2.55,\"coef2\":1.47,\"result\":\"0-0(2-0)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:36:35.954\"},{\"coef1\":2.75,\"coef2\":1.42,\"result\":\"0-0(2-1)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:36:47.093\"},{\"coef1\":3.9,\"coef2\":1.23,\"result\":\"0-0(2-2)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:37:09.412\"},{\"coef1\":3.55,\"coef2\":1.27,\"result\":\"0-0(3-2)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:37:31.731\"},{\"coef1\":3.9,\"coef2\":1.23,\"result\":\"0-0(3-3)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:37:54.098\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"0-0(3-4)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:38:16.928\"},{\"coef1\":3.2,\"coef2\":1.32,\"result\":\"0-0(3-5)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:38:39.614\"},{\"coef1\":3.05,\"coef2\":1.35,\"result\":\"0-0(3-6)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:39:02.150\"},{\"coef1\":3.2,\"coef2\":1.32,\"result\":\"0-0(3-7)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:40:32.560\"},{\"coef1\":3.15,\"coef2\":1.33,\"result\":\"0-0(4-7)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:40:44.185\"},{\"coef1\":3.2,\"coef2\":1.32,\"result\":\"0-0(4-8)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:41:17.687\"},{\"coef1\":3.55,\"coef2\":1.27,\"result\":\"0-0(4-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:41:28.798\"},{\"coef1\":3.75,\"coef2\":1.25,\"result\":\"0-0(4-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:41:51.320\"},{\"coef1\":3.9,\"coef2\":1.23,\"result\":\"0-0(4-10)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:42:02.570\"},{\"coef1\":4.0,\"coef2\":1.22,\"result\":\"0-0(4-11)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:42:36.955\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"0-0(4-12)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:43:21.596\"},{\"coef1\":5.0,\"coef2\":1.15,\"result\":\"0-0(4-12)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:43:55.060\"},{\"coef1\":5.0,\"coef2\":1.15,\"result\":\"0-0(5-12)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:44:50.384\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"0-0(6-12)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:45:14.402\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"0-0(7-12)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:45:36.734\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"0-0(7-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:45:59.236\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"0-0(8-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:46:32.680\"},{\"coef1\":4.5,\"coef2\":1.18,\"result\":\"0-0(9-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:46:55.117\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"0-0(10-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:47:28.546\"},{\"coef1\":3.75,\"coef2\":1.25,\"result\":\"0-0(10-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:47:39.675\"},{\"coef1\":3.25,\"coef2\":1.31,\"result\":\"0-0(11-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:47:50.841\"},{\"coef1\":2.95,\"coef2\":1.37,\"result\":\"0-0(11-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:48:13.127\"},{\"coef1\":3.15,\"coef2\":1.33,\"result\":\"0-0(11-14)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:49:08.886\"},{\"coef1\":3.3,\"coef2\":1.3,\"result\":\"0-0(11-15)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:49:20.742\"},{\"coef1\":3.45,\"coef2\":1.28,\"result\":\"0-0(11-16)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:49:55.170\"},{\"coef1\":3.45,\"coef2\":1.28,\"result\":\"0-0(12-16)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:50:17.615\"},{\"coef1\":3.25,\"coef2\":1.31,\"result\":\"0-0(13-16)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:50:39.746\"},{\"coef1\":3.05,\"coef2\":1.35,\"result\":\"0-0(14-16)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:51:18.539\"},{\"coef1\":2.75,\"coef2\":1.42,\"result\":\"0-0(14-16)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:51:29.666\"},{\"coef1\":2.55,\"coef2\":1.47,\"result\":\"0-0(15-16)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:51:40.890\"},{\"coef1\":2.75,\"coef2\":1.42,\"result\":\"0-0(15-17)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:52:26.312\"},{\"coef1\":2.95,\"coef2\":1.37,\"result\":\"0-0(15-18)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:53:21.595\"},{\"coef1\":2.25,\"coef2\":1.6,\"result\":\"0-0(15-18)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:53:32.654\"},{\"coef1\":2.42,\"coef2\":1.52,\"result\":\"0-0(15-19)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:53:43.722\"},{\"coef1\":2.53,\"coef2\":1.48,\"result\":\"0-0(15-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:54:17.439\"},{\"coef1\":2.48,\"coef2\":1.5,\"result\":\"0-0(16-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:54:50.706\"},{\"coef1\":2.53,\"coef2\":1.48,\"result\":\"0-0(16-21)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:55:35.236\"},{\"coef1\":2.75,\"coef2\":1.42,\"result\":\"0-0(17-21)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:55:57.515\"},{\"coef1\":2.8,\"coef2\":1.4,\"result\":\"0-0(17-22)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:56:08.532\"},{\"coef1\":2.75,\"coef2\":1.42,\"result\":\"0-0(18-22)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:56:41.577\"},{\"coef1\":2.8,\"coef2\":1.4,\"result\":\"0-0(18-23)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:57:03.648\"},{\"coef1\":2.8,\"coef2\":1.4,\"result\":\"0-0(18-24)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:57:48.038\"},{\"coef1\":2.8,\"coef2\":1.4,\"result\":\"0-0(19-24)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:58:21.287\"},{\"coef1\":2.8,\"coef2\":1.4,\"result\":\"0-1(19-25,0-0)\",\"enabled\":null,\"resultTime\":\"2018-02-12T21:58:43.418\"},{\"coef1\":4.0,\"coef2\":1.22,\"result\":\"0-1(19-25,0-0)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:01:56.413\"},{\"coef1\":5.4,\"coef2\":1.13,\"result\":\"0-1(19-25,3-7)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:08:06.598\"},{\"coef1\":5.8,\"coef2\":1.11,\"result\":\"0-1(19-25,3-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:08:28.701\"},{\"coef1\":5.8,\"coef2\":1.11,\"result\":\"0-1(19-25,4-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:08:39.669\"},{\"coef1\":7.0,\"coef2\":1.08,\"result\":\"0-1(19-25,4-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:09:01.597\"},{\"coef1\":6.5,\"coef2\":1.09,\"result\":\"0-1(19-25,5-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:09:12.678\"},{\"coef1\":6.25,\"coef2\":1.1,\"result\":\"0-1(19-25,6-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:09:23.969\"},{\"coef1\":5.55,\"coef2\":1.12,\"result\":\"0-1(19-25,7-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:09:56.831\"},{\"coef1\":6.25,\"coef2\":1.1,\"result\":\"0-1(19-25,7-10)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:10:07.863\"},{\"coef1\":6.5,\"coef2\":1.09,\"result\":\"0-1(19-25,7-11)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:10:40.900\"},{\"coef1\":6.25,\"coef2\":1.1,\"result\":\"0-1(19-25,8-11)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:11:14.087\"},{\"coef1\":5.8,\"coef2\":1.11,\"result\":\"0-1(19-25,9-11)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:11:36.222\"},{\"coef1\":5.4,\"coef2\":1.13,\"result\":\"0-1(19-25,10-11)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:11:58.105\"},{\"coef1\":5.0,\"coef2\":1.15,\"result\":\"0-1(19-25,11-11)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:12:20.205\"},{\"coef1\":3.55,\"coef2\":1.27,\"result\":\"0-1(19-25,11-11)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:12:31.402\"},{\"coef1\":3.9,\"coef2\":1.23,\"result\":\"0-1(19-25,11-12)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:13:04.426\"},{\"coef1\":3.55,\"coef2\":1.27,\"result\":\"0-1(19-25,12-12)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:13:26.484\"},{\"coef1\":3.9,\"coef2\":1.23,\"result\":\"0-1(19-25,12-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:13:49.663\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"0-1(19-25,12-14)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:14:22.781\"},{\"coef1\":4.0,\"coef2\":1.22,\"result\":\"0-1(19-25,13-14)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:14:33.788\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"0-1(19-25,13-15)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:14:55.835\"},{\"coef1\":4.0,\"coef2\":1.22,\"result\":\"0-1(19-25,14-15)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:15:08.834\"},{\"coef1\":4.5,\"coef2\":1.18,\"result\":\"0-1(19-25,14-16)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:15:42.217\"},{\"coef1\":4.0,\"coef2\":1.22,\"result\":\"0-1(19-25,15-16)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:16:26.532\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"0-1(19-25,13-15)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:16:37.917\"},{\"coef1\":4.0,\"coef2\":1.22,\"result\":\"0-1(19-25,15-16)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:16:49.683\"},{\"coef1\":4.5,\"coef2\":1.18,\"result\":\"0-1(19-25,15-17)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:17:00.850\"},{\"coef1\":5.0,\"coef2\":1.15,\"result\":\"0-1(19-25,15-18)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:17:23.287\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"0-1(19-25,16-18)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:17:34.419\"},{\"coef1\":4.0,\"coef2\":1.22,\"result\":\"0-1(19-25,17-18)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:18:08.577\"},{\"coef1\":4.5,\"coef2\":1.18,\"result\":\"0-1(19-25,17-19)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:18:19.875\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"0-1(19-25,18-19)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:19:16.279\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"0-1(19-25,18-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:19:49.234\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"0-1(19-25,19-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:20:55.291\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"0-1(19-25,19-21)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:21:28.438\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"0-1(19-25,20-21)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:21:39.502\"},{\"coef1\":3.55,\"coef2\":1.27,\"result\":\"0-1(19-25,21-21)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:22:01.445\"},{\"coef1\":3.05,\"coef2\":1.35,\"result\":\"0-1(19-25,22-21)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:22:12.442\"},{\"coef1\":3.3,\"coef2\":1.3,\"result\":\"0-1(19-25,22-22)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:23:29.819\"},{\"coef1\":2.95,\"coef2\":1.37,\"result\":\"0-1(19-25,23-22)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:24:02.713\"},{\"coef1\":3.3,\"coef2\":1.3,\"result\":\"0-1(19-25,23-23)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:24:35.863\"},{\"coef1\":2.8,\"coef2\":1.4,\"result\":\"0-1(19-25,24-23)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:25:09.118\"},{\"coef1\":2.25,\"coef2\":1.6,\"result\":\"1-1(19-25,25-23,0-0)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:26:47.827\"},{\"coef1\":2.11,\"coef2\":1.68,\"result\":\"1-1(19-25,25-23,1-0)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:31:03.564\"},{\"coef1\":2.25,\"coef2\":1.6,\"result\":\"1-1(19-25,25-23,1-1)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:31:25.900\"},{\"coef1\":2.42,\"coef2\":1.52,\"result\":\"1-1(19-25,25-23,1-2)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:31:48.553\"},{\"coef1\":2.25,\"coef2\":1.6,\"result\":\"1-1(19-25,25-23,2-2)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:32:10.473\"},{\"coef1\":2.42,\"coef2\":1.52,\"result\":\"1-1(19-25,25-23,2-3)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:32:32.671\"},{\"coef1\":2.25,\"coef2\":1.6,\"result\":\"1-1(19-25,25-23,3-3)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:33:16.811\"},{\"coef1\":2.08,\"coef2\":1.7,\"result\":\"1-1(19-25,25-23,4-3)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:34:01.648\"},{\"coef1\":2.15,\"coef2\":1.65,\"result\":\"1-1(19-25,25-23,4-4)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:34:23.657\"},{\"coef1\":2.35,\"coef2\":1.55,\"result\":\"1-1(19-25,25-23,4-5)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:34:56.782\"},{\"coef1\":2.25,\"coef2\":1.6,\"result\":\"1-1(19-25,25-23,5-5)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:35:20.936\"},{\"coef1\":2.35,\"coef2\":1.55,\"result\":\"1-1(19-25,25-23,5-6)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:35:54.572\"},{\"coef1\":2.25,\"coef2\":1.6,\"result\":\"1-1(19-25,25-23,5-6)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:36:05.522\"},{\"coef1\":2.55,\"coef2\":1.47,\"result\":\"1-1(19-25,25-23,5-7)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:36:28.437\"},{\"coef1\":2.42,\"coef2\":1.52,\"result\":\"1-1(19-25,25-23,6-7)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:36:50.725\"},{\"coef1\":2.55,\"coef2\":1.47,\"result\":\"1-1(19-25,25-23,6-8)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:37:24.040\"},{\"coef1\":2.8,\"coef2\":1.4,\"result\":\"1-1(19-25,25-23,6-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:37:46.418\"},{\"coef1\":3.05,\"coef2\":1.35,\"result\":\"1-1(19-25,25-23,6-10)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:38:19.293\"},{\"coef1\":3.9,\"coef2\":1.23,\"result\":\"1-1(19-25,25-23,6-11)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:39:48.845\"},{\"coef1\":3.75,\"coef2\":1.25,\"result\":\"1-1(19-25,25-23,7-11)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:40:12.008\"},{\"coef1\":3.9,\"coef2\":1.23,\"result\":\"1-1(19-25,25-23,7-12)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:40:34.452\"},{\"coef1\":4.0,\"coef2\":1.22,\"result\":\"1-1(19-25,25-23,7-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:40:56.933\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"1-1(19-25,25-23,7-14)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:41:19.547\"},{\"coef1\":4.5,\"coef2\":1.18,\"result\":\"1-1(19-25,25-23,7-15)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:42:38.286\"},{\"coef1\":4.5,\"coef2\":1.18,\"result\":\"1-1(19-25,25-23,7-16)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:42:49.467\"},{\"coef1\":4.5,\"coef2\":1.18,\"result\":\"1-1(19-25,25-23,7-17)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:43:23.804\"},{\"coef1\":5.0,\"coef2\":1.15,\"result\":\"1-1(19-25,25-23,7-17)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:43:46.609\"},{\"coef1\":5.0,\"coef2\":1.15,\"result\":\"1-1(19-25,25-23,7-18)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:44:00.019\"},{\"coef1\":5.0,\"coef2\":1.15,\"result\":\"1-1(19-25,25-23,7-19)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:44:34.663\"},{\"coef1\":5.0,\"coef2\":1.15,\"result\":\"1-1(19-25,25-23,7-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:45:09.307\"},{\"coef1\":5.55,\"coef2\":1.12,\"result\":\"1-1(19-25,25-23,7-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:45:31.365\"},{\"coef1\":5.55,\"coef2\":1.12,\"result\":\"1-1(19-25,25-23,8-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:45:42.394\"},{\"coef1\":5.55,\"coef2\":1.12,\"result\":\"1-1(19-25,25-23,8-21)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:46:04.626\"},{\"coef1\":5.55,\"coef2\":1.12,\"result\":\"1-1(19-25,25-23,8-22)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:46:26.862\"},{\"coef1\":5.55,\"coef2\":1.12,\"result\":\"1-1(19-25,25-23,8-23)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:47:11.748\"},{\"coef1\":5.55,\"coef2\":1.12,\"result\":\"1-1(19-25,25-23,8-24)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:47:34.018\"},{\"coef1\":5.55,\"coef2\":1.12,\"result\":\"1-1(19-25,25-23,8-23)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:47:44.990\"},{\"coef1\":8.5,\"coef2\":1.05,\"result\":\"1-2(19-25,25-23,8-25,0-0)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:47:55.969\"},{\"coef1\":10.0,\"coef2\":1.03,\"result\":\"1-2(19-25,25-23,8-25,0-0)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:48:42.426\"},{\"coef1\":12.6,\"coef2\":1.01,\"result\":\"1-2(19-25,25-23,8-25,0-2)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:51:33.946\"},{\"coef1\":12.6,\"coef2\":1.01,\"result\":\"1-2(19-25,25-23,8-25,1-2)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:52:06.991\"},{\"coef1\":12.6,\"coef2\":1.01,\"result\":\"1-2(19-25,25-23,8-25,1-3)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:52:28.996\"},{\"coef1\":12.6,\"coef2\":1.01,\"result\":\"1-2(19-25,25-23,8-25,2-3)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:52:51.036\"},{\"coef1\":10.0,\"coef2\":1.03,\"result\":\"1-2(19-25,25-23,8-25,3-3)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:53:23.924\"},{\"coef1\":11.0,\"coef2\":1.02,\"result\":\"1-2(19-25,25-23,8-25,3-4)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:53:45.933\"},{\"coef1\":12.6,\"coef2\":1.01,\"result\":\"1-2(19-25,25-23,8-25,3-5)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:54:18.927\"},{\"coef1\":11.0,\"coef2\":1.02,\"result\":\"1-2(19-25,25-23,8-25,4-5)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:54:52.226\"},{\"coef1\":10.0,\"coef2\":1.03,\"result\":\"1-2(19-25,25-23,8-25,5-5)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:55:03.131\"},{\"coef1\":11.0,\"coef2\":1.02,\"result\":\"1-2(19-25,25-23,8-25,5-6)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:55:46.964\"},{\"coef1\":12.6,\"coef2\":1.01,\"result\":\"1-2(19-25,25-23,8-25,5-7)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:56:09.030\"},{\"coef1\":11.0,\"coef2\":1.02,\"result\":\"1-2(19-25,25-23,8-25,6-7)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:56:41.979\"},{\"coef1\":10.0,\"coef2\":1.03,\"result\":\"1-2(19-25,25-23,8-25,7-7)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:57:04.073\"},{\"coef1\":8.5,\"coef2\":1.05,\"result\":\"1-2(19-25,25-23,8-25,8-7)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:57:48.047\"},{\"coef1\":7.0,\"coef2\":1.08,\"result\":\"1-2(19-25,25-23,8-25,9-7)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:57:58.994\"},{\"coef1\":7.5,\"coef2\":1.07,\"result\":\"1-2(19-25,25-23,8-25,9-8)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:58:21.369\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"1-2(19-25,25-23,8-25,9-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:58:54.449\"},{\"coef1\":3.9,\"coef2\":1.23,\"result\":\"1-2(19-25,25-23,8-25,10-9)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:59:05.399\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"1-2(19-25,25-23,8-25,10-10)\",\"enabled\":null,\"resultTime\":\"2018-02-12T22:59:38.463\"},{\"coef1\":3.9,\"coef2\":1.23,\"result\":\"1-2(19-25,25-23,8-25,11-10)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:00:00.331\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"1-2(19-25,25-23,8-25,11-11)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:00:33.214\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"1-2(19-25,25-23,8-25,11-12)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:01:06.258\"},{\"coef1\":5.2,\"coef2\":1.14,\"result\":\"1-2(19-25,25-23,8-25,11-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:01:28.125\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"1-2(19-25,25-23,8-25,12-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:03:08.575\"},{\"coef1\":5.2,\"coef2\":1.14,\"result\":\"1-2(19-25,25-23,8-25,12-14)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:03:41.362\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"1-2(19-25,25-23,8-25,12-13)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:03:52.363\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"1-2(19-25,25-23,8-25,13-14)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:04:03.405\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"1-2(19-25,25-23,8-25,14-15)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:04:58.567\"},{\"coef1\":4.7,\"coef2\":1.17,\"result\":\"1-2(19-25,25-23,8-25,16-17)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:06:58.685\"},{\"coef1\":4.2,\"coef2\":1.2,\"result\":\"1-2(19-25,25-23,8-25,17-17)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:07:09.780\"},{\"coef1\":4.5,\"coef2\":1.18,\"result\":\"1-2(19-25,25-23,8-25,17-18)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:07:32.028\"},{\"coef1\":4.0,\"coef2\":1.22,\"result\":\"1-2(19-25,25-23,8-25,18-18)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:08:11.205\"},{\"coef1\":4.5,\"coef2\":1.18,\"result\":\"1-2(19-25,25-23,8-25,18-19)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:08:33.102\"},{\"coef1\":5.4,\"coef2\":1.13,\"result\":\"1-2(19-25,25-23,8-25,18-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:09:10.979\"},{\"coef1\":11.0,\"coef2\":1.02,\"result\":\"1-2(19-25,25-23,8-25,18-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:10:26.691\"},{\"coef1\":10.0,\"coef2\":1.03,\"result\":\"1-2(19-25,25-23,8-25,19-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:10:42.280\"},{\"coef1\":7.0,\"coef2\":1.08,\"result\":\"1-2(19-25,25-23,8-25,20-20)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:11:04.214\"},{\"coef1\":8.5,\"coef2\":1.05,\"result\":\"1-2(19-25,25-23,8-25,20-21)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:12:22.159\"},{\"coef1\":12.6,\"coef2\":1.01,\"result\":\"1-2(19-25,25-23,8-25,20-22)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:12:55.268\"},{\"coef1\":0.0,\"coef2\":0.0,\"result\":\"1-2(19-25,25-23,8-25,20-23)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:13:28.261\"},{\"coef1\":0.0,\"coef2\":0.0,\"result\":\"1-2(19-25,25-23,8-25,20-24)\",\"enabled\":null,\"resultTime\":\"2018-02-12T23:13:50.302\"}]";
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedList<MomentResult> resultList= objectMapper.readValue(results, new TypeReference<LinkedList<MomentResult>>() {
        });
        System.out.println(checkFirst5Results(resultList));

    }

    public static Boolean validateSport(String text) {
        if(Constants.SPORTS.contains(text)){
            return true;
        }

        return false;
    }

    public static Boolean validateCompetitions(String competitions){
        for (String stopWord : Constants.STOP_WORDS) {
            if(containsAllWords(competitions, stopWord)){
                return false;
            }
        }

        return true;
    }

    private static Boolean containsAllWords(String str, String stopWord){
        String[] words = stopWord.toLowerCase().split("\\s");
        str = str.toLowerCase();

        for (String word : words) {
            if(!str.contains(word)){
                return false;
            }
        }
        return true;
    }

    //Outcome "Morocco - Angola:Win 2" - the odds have been changed to "1.80".
    public static Boolean checkCoefAndNoBetInProgress(FirefoxDriver driver) {
        try {
            if (driver.getPageSource().contains("have been changed")) {
                String changeCoefText = driver.findElement(By.cssSelector("#stakeHolder ol li")).getText();
                //String changeCoefText = "Outcome \"Morocco - Angola:Win 2\" - the odds have been changed to \"1.18\".";
                changeCoefText = changeCoefText.substring(changeCoefText.indexOf("to \"") + 4);
                changeCoefText = changeCoefText.replaceAll("\".$", "");
                Double changedCoef = Double.valueOf(changeCoefText);
                System.err.println("Coef in new MESSAGE " + changedCoef);
                if (changedCoef > Constants.FIFTH_STAGE_COEF) {
                    return false;
                } else {
                    return true;
                }
            }

            WebElement coefElement = driver.findElement(By.cssSelector("#wb .td_cf"));
            Double coef = Double.valueOf(coefElement.getText());
            System.err.println("COEF in TABLE " + coef);

            if (coef > Constants.FIFTH_STAGE_COEF) {
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return false;
    }

    public static Double getCurrentCoef(FirefoxDriver driver){
        try {
            if (driver.getPageSource().contains("have been changed")) {
                String changeCoefText = driver.findElement(By.cssSelector("#stakeHolder ol li")).getText();
                //String changeCoefText = "Outcome \"Morocco - Angola:Win 2\" - the odds have been changed to \"1.18\".";
                changeCoefText = changeCoefText.substring(changeCoefText.indexOf("to \"") + 4);
                changeCoefText = changeCoefText.replaceAll("\".$", "");
                Double changedCoef = Double.valueOf(changeCoefText);
                System.err.println("Coef in new MESSAGE " + changedCoef);
                return changedCoef;
            }

            WebElement coefElement = driver.findElement(By.cssSelector("#wb .td_cf"));
            Double coef = Double.valueOf(coefElement.getText());
            System.err.println("COEF in TABLE " + coef);

            return coef;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return -1.0;
    }

    public static boolean checkFirst5Results(List<MomentResult> results){
        if(results.size()< 5 ){
            return false;
        }

        int firstFavorite = results.get(0).getCoef1() > results.get(0).getCoef2() ? 0 : 1;

        for (int i = 1; i < 5; i++) {
            if(firstFavorite == 0){
                if(results.get(i).getCoef1() < results.get(i).getCoef2()){
                    return false;
                }
            }
            if(firstFavorite == 1){
                if(results.get(i).getCoef1() > results.get(i).getCoef2()){
                    return false;
                }
            }
        }

        return true;
    }

}
