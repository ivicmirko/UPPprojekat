import { Component, OnInit } from '@angular/core';
import { WorkService } from '../services/Work/work.service';
import { SessionStorageService } from '../services/SessionStorage/session-storage.service';
import { Router } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { EditorWorkService } from '../services/EditorWork/editor-work.service';

@Component({
  selector: 'app-arrived-works',
  templateUrl: './arrived-works.component.html',
  styleUrls: ['./arrived-works.component.scss']
})
export class ArrivedWorksComponent implements OnInit {

  private showList=true;
  private processInstance="";
  private formFieldsDto = null;
  private formFields = [];
  private arrivedWorks=[];
  private processId:String;
  private workId:Number;
  private title="";
  fileUrl

  constructor(private router:Router,private sanitizer: DomSanitizer,
    private editorWorkService:EditorWorkService, private sessionStorageService:SessionStorageService) {
    
      this.editorWorkService.getArrivedWorks().subscribe(
      res=>{
        console.log(res);
        this.arrivedWorks=res;
      },
      error=>{
        console.log(error);
      }
    );
   }

   

  ngOnInit() {
    this.processId=this.sessionStorageService.getProcessId();
  
  }

  getCheckWorkForm(workId,processId,title){
    this.editorWorkService.getCheckWorkForm(processId).subscribe(
      res=>{
        this.title=title;
        this.workId=workId;
        this.processId=processId;
        console.log(res);
        this.formFieldsDto = res;
        this.formFields = res.formFields;
        this.processInstance = res.processInstanceId;
        this.showList=false;
      },
      error=>{
        console.log(error);
      }
    );
  }

  approveWorkDetails(){
    this.editorWorkService.approveWorkDetails(this.processId,this.workId).subscribe(
      res=>{
        console.log("Preuzima PDF");
        console.log(res);
        
        let blob = new Blob([res], { type: 'blob' });
        let url= window.URL.createObjectURL(res);
        
        var link = document.createElement('a');
            link.href = url;
            link.download = this.title+".pdf";
            // this is necessary as link.click() does not work on the latest firefox
            link.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));

 
        //window.open(url);
        //saveAs(blob,this.title+".pdf");
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.router.onSameUrlNavigation = 'reload';
        this.router.navigateByUrl('/arrivedWorks');
        
      },
      error=>{
        console.log(error);
      }
    );
  }

  denyWorkDetails(){
    this.editorWorkService.denyWorkDetails(this.processId,this.workId).subscribe(
      res=>{
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.router.onSameUrlNavigation = 'reload';
        this.router.navigateByUrl('/arrivedWorks');
      },
      error=>{
        console.log(error);
      }
    )
  }



}
