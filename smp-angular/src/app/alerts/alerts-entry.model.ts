export interface AlertsEntry {
  processed: boolean;
  alertId: string;
  alertType: string;
  alertLevel: string;
  alertText: string;
  creationTime: Date;
  reportingTime: Date;
  parameters: string[];
}
