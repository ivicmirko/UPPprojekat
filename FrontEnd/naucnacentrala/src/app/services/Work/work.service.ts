import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
const url='http://localhost:8081/work/';

@Injectable({
  providedIn: 'root'
})
export class WorkService {

  private getChoosingMagazineFormUrl=url+"getChoosingMagazineForm";
  private postChoosingMagazineFormUrl=url+"postChoosingMagazineForm/";
  private getFormForWorkDetailsUrl=url+"getFormForWorkDetails/";
  private postFormForWorkDetailsUrl=url+"postFormForWorkingDetails/";
  private getCoAuthorsFormUrl=url+"getCoAuthorsForm/";
  private postPdfFileUrl=url+"postPdfFile/";
  private postCoauthorFormUrl=url+"postCoauthorForm/";
  
  private getWorksForPdfFixUrl=url+"getWorksForPdfFormatFix";
  private getWorkForPdfFormatFixUrl=url+"getWorkForPdfFormatFix/"
  private sendNewPdfUrl=url+"sendFixedPdfFormatFile/"


  constructor(private http:HttpClient) { }


  getChoosingMagazineForm():Observable<any>{
    return this.http.get(this.getChoosingMagazineFormUrl);
  }

  postChoosingMagazine(processId,taskId,o):Observable<any>{
    return this.http.post(this.postChoosingMagazineFormUrl+taskId+"/"+processId,o);
  }

  getFormForWorkDetails(processId):Observable<any>{
    return this.http.get(this.getFormForWorkDetailsUrl+processId);
  }

  postFormForWorkDetails(processId,taskId,o):Observable<any>{
    
    return this.http.post(this.postFormForWorkDetailsUrl+taskId+"/"+processId,o);
  }

  sendFile(pdfFile,processId){
    const formData:FormData=new FormData();
    //let ret=new Array();
    //ret.push({pdfWorkForma:pdfFile});
    //ret.push({dataForm:o});
    formData.append("pdfWork",pdfFile);
    return this.http.post(this.postPdfFileUrl+processId,formData);

    console.log(formData);
  }

  getCoAuthorsForm(processId):Observable<any>{
    return this.http.get(this.getCoAuthorsFormUrl+processId);
  }

  postCoAuthorForm(processId,taskId,o):Observable<any>{
    return this.http.post(this.postCoauthorFormUrl+taskId+"/"+processId,o);
  }

  getWorksForPdfFix():Observable<any>{
    return this.http.get(this.getWorksForPdfFixUrl);
  }

  getWorkForPdfFix(processId):Observable<any>{
    return this.http.get(this.getWorkForPdfFormatFixUrl+processId);
  }

  sendNewPdfFormatFix(processId,taskId,pdfWork):Observable<any>{
    const formData:FormData=new FormData();
    formData.append("pdfWork",pdfWork);
    return this.http.post(this.sendNewPdfUrl+processId+"/"+taskId,formData);
  }


}
