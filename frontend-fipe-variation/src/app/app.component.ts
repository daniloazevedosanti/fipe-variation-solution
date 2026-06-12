import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { catchError, finalize, of, switchMap, tap } from 'rxjs';
import { FipeOption, VehicleTypeOption, VehicleVariationResponse } from './models/vehicle.models';
import { FipeApiService } from './services/fipe-api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(FipeApiService);

  readonly vehicleTypes = signal<VehicleTypeOption[]>([]);
  readonly brands = signal<FipeOption[]>([]);
  readonly models = signal<FipeOption[]>([]);
  readonly result = signal<VehicleVariationResponse | null>(null);
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly hasResult = computed(() => (this.result()?.items.length ?? 0) > 0);

  readonly form = this.fb.nonNullable.group({
    vehicleType: ['cars', Validators.required],
    brandId: ['', Validators.required],
    modelId: ['', Validators.required],
    reference: this.fb.control<number | null>(null)
  });

  ngOnInit(): void {
    this.loadVehicleTypes();
    this.loadBrands();

    this.form.controls.vehicleType.valueChanges
      .pipe(
        tap(() => this.resetSelection()),
        switchMap((vehicleType) => this.api.getBrands(vehicleType, this.form.controls.reference.value)),
        catchError((error) => this.handleListError<FipeOption[]>(error, []))
      )
      .subscribe((brands) => this.brands.set(brands));

    this.form.controls.brandId.valueChanges
      .pipe(
        tap(() => {
          this.form.controls.modelId.reset('');
          this.models.set([]);
          this.result.set(null);
        }),
        switchMap((brandId) => {
          if (!brandId) {
            return of([]);
          }
          return this.api.getModels(this.form.controls.vehicleType.value, brandId, this.form.controls.reference.value);
        }),
        catchError((error) => this.handleListError<FipeOption[]>(error, []))
      )
      .subscribe((models) => this.models.set(models));
  }

  search(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);
    this.result.set(null);

    const value = this.form.getRawValue();
    this.api.getVariations(value.vehicleType, value.brandId, value.modelId, value.reference)
      .pipe(
        finalize(() => this.loading.set(false)),
        catchError((error) => {
          this.errorMessage.set(this.extractErrorMessage(error));
          return of(null);
        })
      )
      .subscribe((response) => this.result.set(response));
  }

  reloadBrands(): void {
    this.resetSelection();
    this.loadBrands();
  }

  private loadVehicleTypes(): void {
    this.api.getVehicleTypes()
      .pipe(catchError((error) => this.handleListError<VehicleTypeOption[]>(error, [])))
      .subscribe((types) => this.vehicleTypes.set(types));
  }

  private loadBrands(): void {
    this.loading.set(true);
    this.api.getBrands(this.form.controls.vehicleType.value, this.form.controls.reference.value)
      .pipe(
        finalize(() => this.loading.set(false)),
        catchError((error) => this.handleListError<FipeOption[]>(error, []))
      )
      .subscribe((brands) => this.brands.set(brands));
  }

  private resetSelection(): void {
    this.form.controls.brandId.reset('');
    this.form.controls.modelId.reset('');
    this.brands.set([]);
    this.models.set([]);
    this.result.set(null);
    this.errorMessage.set(null);
  }

  private handleListError<T>(error: unknown, fallback: T) {
    this.errorMessage.set(this.extractErrorMessage(error));
    return of(fallback);
  }

  private extractErrorMessage(error: any): string {
    return error?.error?.detail ?? 'Não foi possível consultar os dados. Verifique se o backend está em execução.';
  }
}
