import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {MainComponent} from "./main/main.component";
import {AuthGuard} from "./auth/auth-guard.service";

const routes: Routes = [
  {path: '' , component: LoginComponent},
  {path: 'main' , component: MainComponent, canActivate: [AuthGuard]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
