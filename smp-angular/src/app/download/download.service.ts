import {Injectable} from "@angular/core";

@Injectable()
export class DownloadService {

  downloadNative(content) {
    let element = document.createElement('a');
    element.setAttribute('href', content);
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
  }
}
