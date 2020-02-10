import { Component, OnInit } from '@angular/core';
import { FormGroupDirective, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { WorkService } from '../services/Work/work.service';
import { Router } from '@angular/router';
import { EditorWorkService } from '../services/EditorWork/editor-work.service';
import { EnumValue } from '../model/enum-value';

@Component({
  selector: 'app-add-work-reviewers',
  templateUrl: './add-work-reviewers.component.html',
  styleUrls: ['./add-work-reviewers.component.scss']
})
export class AddWorkReviewersComponent implements OnInit {

  private showList=true;
  private toSetDate=false;
  private processInstance="";
  private formFieldsDto = null;
  private formFields = [];
  private works=[];
  private processId:String;
  private workId:Number;
  private title="";
  private setDateForm:FormGroup;
  private chooseReviewersForm:FormGroup;

  private enumKeys=[];
  private enumValues = [];
  private enumObjects=[];
  private enumObject:EnumValue;

  private submitted=false;
  private success=false;

  private submitted2=false;
  private success2=false;

  private formFieldsDto2 = null;
  private formFields2 = [];

  constructor(private workService:WorkService,private formBuilder:FormBuilder,private router:Router,
    private editorWorkService:EditorWorkService) { }

  ngOnInit() {
    this.editorWorkService.getWorksWithoutReviewers().subscribe(
      res=>{
        this.works=res;
      },
      error=>{
        console.log(error);
      }
    );

    this.setDateForm=this.formBuilder.group({
      reviewTime: ['',Validators.required],
    });

    this.chooseReviewersForm=this.formBuilder.group({
      chooseReviewers: ['',Validators.minLength(2)],
    });
  }

  downloadWorkPdf(id,processId,title){
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

  getReviewWorkForm(id,processId,title){
    this.workId=id;
    this.processId=processId;
    this.editorWorkService.getAddReviewersForm(processId).subscribe(
      res=>{
        this.showList=false;
        console.log(res);
        this.formFieldsDto = res;
        this.formFields = res.formFields;
        this.processInstance = res.processInstanceId;
        this.formFields.forEach( (field) =>{
            
          if(field.type.name=='enum'){
            this.enumKeys =Object.keys(field.properties);
            this.enumValues =Object.values(field.properties);
            for(let i=0; i<this.enumKeys.length; i++){
              this.enumObject=new EnumValue(this.enumKeys[i],this.enumValues[i]);
              this.enumObjects.push(this.enumObject);
            }
          }
        });
      },
      error=>{
        console.log(error);
      }
    );
  }

  chooseReviewers(value,form){
    this.submitted=true;

    if(this.chooseReviewersForm.invalid){
      return;
    }

    this.success=true;

    let o = new Array();
    let temp="";
    for (var property in value) {
      console.log(property);
      console.log(value[property]);
      
      if(property != 'chooseReviewers'){
        o.push({fieldId : property, fieldValue : value[property]});
      }else{
        for(var pom of value[property]){
          temp=temp.concat(pom);
          temp=temp.concat(',');
          
        }
      }
    }
    temp=temp.substring(0,temp.length-1);
    o.push({fieldId : "choseReviewers", fieldValue : temp});

    console.log(o);

    this.editorWorkService.postAddWorksReviewersForm(this.processId,this.formFieldsDto.taskId,o).subscribe(
      res=>{
        console.log(res);
        this.toSetDate=true;
        this.showList=false;
        this.formFieldsDto2 = res;
        this.formFields2 = res.formFields;
        this.processInstance = res.processInstanceId;
      },
      error=>{
        console.log(error);
      }
    );
    
  }

  setDate(value,form){
    this.submitted2=true;

    if(this.setDateForm.invalid){
      return;
    }

    this.success2=true;

    let o = new Array();
    let temp="";
    for (var property in value) {
      console.log(property);
      console.log(value[property]);
        o.push({fieldId : property, fieldValue : value[property]});
    }
  
    
    this.editorWorkService.postSetReviewingTimeForm(this.processId,this.formFieldsDto2.taskId,o).subscribe(
      res=>{
        this.router.routeReuseStrategy.shouldReuseRoute = () => false;
        this.router.onSameUrlNavigation = 'reload';
        this.router.navigateByUrl('/addWorkReviewers');
      },
      error=>{
        console.log(error);
      }
    );
  }

}
