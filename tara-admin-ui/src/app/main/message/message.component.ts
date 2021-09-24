import {Component, OnInit} from '@angular/core';
import {MessageService} from "./message.service";

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent implements OnInit {

  constructor(private messageService: MessageService) { }

  ngOnInit(): void {
  }

  hasMessage() {
    return this.messageService.message !== undefined;
  }

  getMessageType() {
    return this.messageService.type
  }

  getMessage() {
    return this.messageService.message
  }

  clearMessage() {
    this.messageService.clearMessage();
  }

}
