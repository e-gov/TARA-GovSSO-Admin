import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {RestClient} from "./http/client";
import {AuthService} from "./auth/auth.service";
import {MainComponent} from "./main/main.component";
import {HeaderComponent} from "./header/header.component";
import {MatTabsModule} from "@angular/material/tabs";
import {BrowserAnimationsModule, NoopAnimationsModule} from "@angular/platform-browser/animations";
import {ClientsComponent} from './clients/clients.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from "@angular/material/icon";
import {MatTableModule} from "@angular/material/table";
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatPaginatorModule} from "@angular/material/paginator";
import {ClientDialogComponent} from './clients/client-dialog/client-dialog.component';
import {MAT_DIALOG_DEFAULT_OPTIONS, MatDialogModule} from "@angular/material/dialog";
import {MAT_DATE_LOCALE, MAT_DATE_FORMATS, MatNativeDateModule, MatOptionModule} from "@angular/material/core";
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatSelectModule} from "@angular/material/select";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {AddValueDialogComponent} from './clients/client-dialog/add-value-dialog/add-value-dialog.component';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {AddTranslationDialogComponent} from "./clients/client-dialog/add-translation-dialog/add-translation-dialog.component";
import {AddContactDialogComponent} from "./clients/client-dialog/add-contact-dialog/add-contact-dialog.component";
import {MatSortModule} from "@angular/material/sort";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatButtonModule} from "@angular/material/button";
import {AuthGuard} from "./auth/auth-guard.service";
import {MessageComponent} from "./main/message/message.component";
import {MessageService} from "./main/message/message.service";
import {ClientsService} from "./clients/clients.service";
import {ChangeSecretDialogComponent} from "./clients/client-dialog/change-secret-dialog/change-secret-dialog.component";
import {VerifyComponent} from "./main/verify/verify.component";
import {InstitutionDialogComponent} from "./institutions/institution-dialog/institution-dialog.component";
import {InstitutionsComponent} from "./institutions/institutions.component";
import {InstitutionsService} from "./institutions/institutions.service";
import {AlertsService} from "./alerts/alerts.service";
import {AlertsComponent} from "./alerts/alerts.component";
import {AlertDialogComponent} from "./alerts/alert-dialog/alert-dialog.component";
import {HttpInterceptorService} from "./http-interceptor.service";
import {InstitutionClientsDialogComponent} from "./institutions/institution-clients-dialog/institution-clients-dialog.component";
import {EditValueDialogComponent} from "./clients/client-dialog/edit-value-dialog/edit-value-dialog.component";

export const DATEPICKER_FORMATS = {
  parse: {
    dateInput: 'DD.MM.YYYY',
  },
  display: {
    dateInput: 'DD.MM.YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};

declare global {
  interface Crypto {
    randomUUID: () => string;
  }
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    MainComponent,
    HeaderComponent,
    ClientsComponent,
    ClientDialogComponent,
    AddValueDialogComponent,
    AddTranslationDialogComponent,
    AddContactDialogComponent,
    ChangeSecretDialogComponent,
    MessageComponent,
    VerifyComponent,
    InstitutionDialogComponent,
    InstitutionClientsDialogComponent,
    InstitutionsComponent,
    AlertsComponent,
    AlertDialogComponent,
    EditValueDialogComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    NoopAnimationsModule,
    MatTabsModule,
    MatFormFieldModule,
    MatIconModule,
    MatTableModule,
    MatInputModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatOptionModule,
    MatSelectModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    MatCheckboxModule,
    MatSortModule,
    BrowserAnimationsModule,
    MatTooltipModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  providers: [RestClient, AuthService, AuthGuard, MessageService, ClientsService, InstitutionsService, AlertsService, HttpInterceptorService, MatDatepickerModule, MatNativeDateModule,
    {provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: {hasBackdrop: false}},
    {provide: MAT_DATE_FORMATS, useValue: DATEPICKER_FORMATS},
    {provide: MAT_DATE_LOCALE, useValue: 'et'},
    {provide: HTTP_INTERCEPTORS, useClass: HttpInterceptorService, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
