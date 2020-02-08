import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { ActivationComponent } from './activation/activation.component';
import { ReviewerConformationComponent } from './reviewer-conformation/reviewer-conformation.component';
import { NewMagazineComponent } from './new-magazine/new-magazine.component';
import { MagazineOverviewComponent } from './magazine-overview/magazine-overview.component';
import { MyMagazineComponent } from './my-magazine/my-magazine.component';
import { AddNewWorkComponent } from './add-new-work/add-new-work.component';
import { ArrivedWorksComponent } from './arrived-works/arrived-works.component';
import { CheckPdfFormatComponent } from './check-pdf-format/check-pdf-format.component';
import { FixingPdfFormatComponent } from './fixing-pdf-format/fixing-pdf-format.component';


const routes: Routes = [
  {path: 'login', component:LoginComponent},
  {path: 'registration', component:RegistrationComponent},
  {
    path: "activation/:username/:processId",
    component: ActivationComponent
  },
  {path: 'reviewerRequests', component:ReviewerConformationComponent},
  {path: 'newmagazine', component:NewMagazineComponent},
  {path: 'newMagazinesRequests', component:MagazineOverviewComponent},
  {path: 'myMagazine', component:MyMagazineComponent},
  {path: 'addNewWork', component:AddNewWorkComponent},
  {path: 'arrivedWorks', component:ArrivedWorksComponent},
  {path: 'checkPDFWorks', component:CheckPdfFormatComponent},
  {path: 'fixingPdfFormat', component:FixingPdfFormatComponent},
  // {path: 'authorNotification', component:AuthorNotification}
  // {path: 'editorNotification', component:EditorNotification}


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
