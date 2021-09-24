export class DateHelper {
  static convertToDisplayString(dateTime: string) {
    let parsedDateTime = this.parseDateTime(dateTime);

    return parsedDateTime.date + " " + parsedDateTime.time;
  }

  static parseDateTime(dateTime: string): { date: string, time: string } {
    if (dateTime == undefined || dateTime === "") {
      return { date: "", time: "" }
    }

    let date = new Date(dateTime);

    return {
      date: date.getDate() + "." + (date.getMonth() + 1) + "." + date.getFullYear(),
      time: String(date.getHours()).padStart(2, "0") + ":" + String(date.getMinutes()).padStart(2, "0") + ":" + String(date.getSeconds()).padStart(2, "0")
    };
  }
}
