import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { FipeOption, VehicleTypeOption, VehicleVariationResponse } from '../models/vehicle.models';

@Injectable({ providedIn: 'root' })
export class FipeApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl;

  getVehicleTypes(): Observable<VehicleTypeOption[]> {
    return this.http.get<VehicleTypeOption[]>(`${this.baseUrl}/vehicle-types`);
  }

  getBrands(vehicleType: string, reference?: number | null): Observable<FipeOption[]> {
    return this.http.get<FipeOption[]>(`${this.baseUrl}/${vehicleType}/brands`, {
      params: this.buildReferenceParam(reference)
    });
  }

  getModels(vehicleType: string, brandId: string, reference?: number | null): Observable<FipeOption[]> {
    return this.http.get<FipeOption[]>(`${this.baseUrl}/${vehicleType}/brands/${brandId}/models`, {
      params: this.buildReferenceParam(reference)
    });
  }

  getVariations(
    vehicleType: string,
    brandId: string,
    modelId: string,
    reference?: number | null
  ): Observable<VehicleVariationResponse> {
    return this.http.get<VehicleVariationResponse>(
      `${this.baseUrl}/${vehicleType}/brands/${brandId}/models/${modelId}/variations`,
      { params: this.buildReferenceParam(reference) }
    );
  }

  private buildReferenceParam(reference?: number | null): HttpParams {
    return reference ? new HttpParams().set('reference', reference) : new HttpParams();
  }
}
