import {Injectable} from "@angular/core";

@Injectable()
export class MessageService {
  type: "INFO" | "ERROR" | "SUCCESS" | undefined;
  message: string | undefined;

  constructor() { }

  showMessage(message: string, type: "INFO" | "ERROR" | "SUCCESS", duration?: number) {
    this.type = type;
    this.message = message;

    if (duration) {
      setTimeout(() => this.clearMessage(), duration)
    }
  }

  clearMessage() {
    this.type = undefined;
    this.message = undefined;
  }
}
